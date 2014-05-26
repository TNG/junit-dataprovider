package com.tngtech.java.junit.dataprovider;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class DataProviderUtils {

    public static Object[] $(Object... args) { // TODO use T -> varargs warning -> can be suppressed here for java > 7!
        return args;
    }

    public static Object[][] $$(Object[]... args) { // TODO use T ??
        return args;
    }

    public static Object[][] testForEach(Object... args) {
        int idx = 0;
        Object[][] result = new Object[args.length][1];
        for (Object arg : args) {
            result[idx++][0] = arg;
        }
        return result;
    }

    public static <T> Object[][] testForEach(Iterable<T> args) {
        if (args == null) {
            throw new NullPointerException("args must not be null");
        }

        List<T> list = new ArrayList<T>();
        for (T arg : args) {
            list.add(arg);
        }
        return testForEach(list.toArray());
    }

    /** */
    public static <E extends Enum<E>> Object[][] testForEach(Class<E> enumClass) {
        if (enumClass == null) {
            throw new NullPointerException("enumClass must not be null");
        }
        // compiler ensure that it is an enum due to type arguments

        try {
            return testForEach((Object[]) enumClass.getMethod("values").invoke(null));

        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        throw new IllegalArgumentException(); // TODO
    }
}
