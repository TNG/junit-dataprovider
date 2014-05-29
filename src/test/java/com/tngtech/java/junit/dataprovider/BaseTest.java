package com.tngtech.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseTest {

    // -- helper methods to find non-mockable Method objects (due to final :-( ) ---------------------------------------
    /**
     * @return a {@link Method} (never {@code null})
     * @throws AssertionError if no {@link Method} could be found
     */
    protected Method anyMethod() {
        return getMethod("anyMethod");
    }

    /**
     * @param methodName name of the {@link Method} to be searched
     * @return the found {@link Method} (never {@code null})
     * @throws AssertionError if {@link Method} could not be found
     */
    protected Method getMethod(String methodName) {
        return getMethodInt(this.getClass(), methodName);
    }

    private Method getMethodInt(Class<?> clazz, String methodName) {
        if (clazz == null) {
            fail(String.format("No method with name '%s' found.", methodName));
            return null;
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return getMethodInt(clazz.getSuperclass(), methodName);
    }

    // -- helper methods to create arrays, lists etc. ------------------------------------------------------------------

    protected <T> List<T> list(T... ts) {
        return Arrays.asList(ts);
    }

    protected <T> List<T[]> listOfArrays(T[]... arrays) {
        List<T[]> result = new ArrayList<T[]>();
        for (T[] array : arrays) {
            result.add(array);
        }
        return result;
    }

    // -- Test data ----------------------------------------------------------------------------------------------------

    protected static enum TestEnum {
        VAL1,
        VAL2,
        VAL3,

        ;
    }
}
