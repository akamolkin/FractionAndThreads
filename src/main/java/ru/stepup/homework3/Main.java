package ru.stepup.homework3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Fraction fr = new Fraction(2,3);
        Fractionable num = (Fractionable) CacheUtils.cache(fr);
        num.doubleValue(); // invoke double value
        num.doubleValue(); // use cache
        num.setNum(1);
        num.doubleValue(); // invoke double value
        num.doubleValue(); // use cache
        num.setNum(3);
        num.intValue(); // invoke int value
        num.setNum(4);
        num.intValue(); // invoke int value

    }
}
