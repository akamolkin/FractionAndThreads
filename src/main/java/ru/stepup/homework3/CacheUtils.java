package ru.stepup.homework3;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class CacheUtils implements InvocationHandler {
    Object curObj;
    // сделал поле статичным, что бы в тесте проверить что отчистка работает и изменился size()
    static Map<State, Map<Method, Result>> states = new HashMap<>();
    Map<Method, Result> curRes;
    State curSt;
    int size = 1; // при привышении этого размера мапы запускаем отчистку

    public CacheUtils(Object obj) {
        this.curObj = obj;
        curRes = new ConcurrentHashMap<>(); // для контроля удаления и добавления
        curSt = new State();
        states.put(curSt, curRes);
    }

    public static int getSize() {
        return states.size();
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object resObj;
        Method m = curObj.getClass().getMethod(method.getName(), method.getParameterTypes());


        if (m.isAnnotationPresent(Cache.class)) {
            Result res = curRes.get(m);
            long time = m.getAnnotation(Cache.class).time();
            if (res != null) {
                res.lifeTime = System.currentTimeMillis() + time;
                if (time == 0) res.lifeTime = 0;
                curRes.put(m, res);
                System.out.println("use cache");
                return res.value;
            }
            resObj = method.invoke(curObj, args);
            res = new Result(System.currentTimeMillis() + time, resObj);
            if (time == 0) res.lifeTime = 0;
            curRes.put(m, res);

            // Если размер больше заданного, то запускаем поток с отчисткой
            if (states.size() > size) {
                System.out.println("Cleaning");
                new CacheClean().start();
            }

            return resObj;
        }
        if (m.isAnnotationPresent(Mutator.class)) {
            // что бы оправдать наличие мутатра будем тут опеределять текущее состочние и результат
            curSt = new State(curSt, m, args);
            if (states.containsKey(curSt)){
                curRes = states.get(curSt);
            } else {
                curRes = new ConcurrentHashMap<>();
                states.put(curSt, curRes);
            }
        }
        return method.invoke(curObj, args);
    }

    public static Object cache(Object obj){
        Class cls = obj.getClass();
        return  Proxy.newProxyInstance(cls.getClassLoader(),
                cls.getInterfaces(),
                new CacheUtils(obj));
    }

    private class CacheClean extends Thread{
        @Override
        public void run() {
            Set<ru.stepup.homework3.State> keys = new HashSet<>(states.keySet());
            for (ru.stepup.homework3.State st : keys) {
                 Map<Method, Result> map = states.get(st);
                 // удаляем результаты
                 for (Method method: map.keySet()) {
                        Result result = map.get(method);
                        if (result.lifeTime == 0) continue;
                        if (result.lifeTime < System.currentTimeMillis()) {
                            map.remove(method);
                        }
                 }
                 // чистим пустые состояния
                if (map.isEmpty()) {
                    states.remove(st);
                }
            }
        }
    }
}
