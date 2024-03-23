package ru.stepup.homework3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class UnitTests {
    @Test
    public void test() {
       // Map<State, Map<Method, Result>> map = new HashMap<>();
        //    Object obj = null;
        //    CacheUtils cacheUtils = new CacheUtils(obj);

        Fraction fr = new Fraction(2,3);
        Fractionable num = (Fractionable) CacheUtils.cache(fr);
        num.doubleValue(); // time = 1
        num.setNum(1);
        num.doubleValue(); // time = 1
        num.setNum(3);
        num.intValue(); // time = 0
        num.setNum(4);
        num.intValue(); // time = 0
        // 4 кэширования, после отчистки должно быть 2 объекта (time = 0)
        Assertions.assertEquals(2, CacheUtils.states.size());
    }
}
