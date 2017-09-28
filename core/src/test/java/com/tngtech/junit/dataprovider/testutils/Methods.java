package com.tngtech.junit.dataprovider.testutils;

import static org.assertj.core.api.Assertions.fail;

import java.lang.reflect.Method;

public class Methods {

    /**
     * @return a {@link Method} (never {@code null})
     * @throws AssertionError if no {@link Method} could be found
     */
    public static Method anyMethod() {
        return getMethod(Methods.class, "anyMethod");
    }

    /**
     * @param clazz to be search for the given method name
     * @param methodName name of the {@link Method} to be searched
     * @return the found {@link Method} (never {@code null})
     * @throws AssertionError if {@link Method} could not be found
     */
    public static Method getMethod(Class<?> clazz, String methodName) {
        return getMethodInt(clazz, methodName);
    }

    private static Method getMethodInt(Class<?> clazz, String methodName) {
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
}
