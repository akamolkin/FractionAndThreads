package ru.stepup.homework3;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
    private final Map<Method, List<Object>> values = new HashMap<>();

    public State(){}
    public State(State st, Method method, Object[] args) {
        values.putAll(st.values);
        values.put(method, Arrays.asList(args));
    }
}
