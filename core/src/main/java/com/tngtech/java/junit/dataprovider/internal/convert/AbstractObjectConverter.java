package com.tngtech.java.junit.dataprovider.internal.convert;

import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkArgument;
import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkNotNull;

import java.lang.reflect.Method;

public abstract class AbstractObjectConverter<V> {

    /**
     * Converts the given {@code data} to its corresponding arguments using the provided information. Additionally
     * checks the arguments against the given parameter types before returning.
     *
     * @param data array of arguments for test method
     * @param isVarArgs determines whether test method has a varargs parameter
     * @param parameterTypes target types of parameters
     * @return {@code Object[]} which is converted for varargs support and checked against {@code parameterTypes}
     * @throws IllegalArgumentException iif the data does not fit somehow
     */
    public abstract Object[] convert(V data, boolean isVarArgs, Class<?>[] parameterTypes);

    /**
     * Checks if the types of the given {@code arguments} matches the given test methods {@code parameterTypes} and throws an {@link Error}
     * if not.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param arguments the arguments to be used for each test method to be executed
     * @param parameterTypes test method parameter types (from {@link Method#getParameterTypes()})
     * @throws NullPointerException iif given {@code parameterTypes} or {@code settings} are {@code null}
     * @throws IllegalArgumentException iif test methods parameter types does not match the given {@code arguments}
     */
    protected void checkIfArgumentsMatchParameterTypes(Object[] arguments, Class<?>[] parameterTypes) {
        checkNotNull(arguments, "arguments must not be null");
        checkNotNull(parameterTypes, "testMethod must not be null");
        checkArgument(parameterTypes.length == arguments.length, "Expected %s arguments for test method but got %s parameters.",
                parameterTypes.length, arguments.length);

        for (int idx = 0; idx < arguments.length; idx++) {
            Object object = arguments[idx];
            if (object != null) {
                Class<?> paramType = parameterTypes[idx];
                if (!paramType.isInstance(object) && !isWrappedInstance(paramType, object)
                        && !isWideningConversion(paramType, object)) {
                    throw new IllegalArgumentException(String.format(
                            "Parameter %d is of type %s but argument given is %s of type %s", idx,
                            paramType.getSimpleName(), object, object.getClass().getSimpleName()));
                }
            }
        }
    }

    private boolean isWrappedInstance(Class<?> clazz, Object object) {
        return (boolean.class.equals(clazz) && Boolean.class.isInstance(object))
                || (byte.class.equals(clazz) && Byte.class.isInstance(object))
                || (char.class.equals(clazz) && Character.class.isInstance(object))
                || (double.class.equals(clazz) && Double.class.isInstance(object))
                || (float.class.equals(clazz) && Float.class.isInstance(object))
                || (int.class.equals(clazz) && Integer.class.isInstance(object))
                || (long.class.equals(clazz) && Long.class.isInstance(object))
                || (short.class.equals(clazz) && Short.class.isInstance(object))
                || (void.class.equals(clazz) && Void.class.isInstance(object));
    }

    private boolean isWideningConversion(Class<?> clazz, Object object) {
        // byte to short, int, long, float, or double
        if ((short.class.equals(clazz) || int.class.equals(clazz) || long.class.equals(clazz)
                || float.class.equals(clazz) || double.class.equals(clazz))
                && Byte.class.isInstance(object)) {
            return true;
        }

        // short or char to int, long, float, or double
        if ((int.class.equals(clazz) || long.class.equals(clazz) || float.class.equals(clazz) || double.class
                .equals(clazz)) && (Short.class.isInstance(object) || Character.class.isInstance(object))) {
            return true;
        }
        // int to long, float, or double
        if ((long.class.equals(clazz) || float.class.equals(clazz) || double.class.equals(clazz))
                && Integer.class.isInstance(object)) {
            return true;
        }
        // long to float or double
        if ((float.class.equals(clazz) || double.class.equals(clazz)) && Long.class.isInstance(object)) {
            return true;
        }
        // float to double
        if ((double.class.equals(clazz)) && Float.class.isInstance(object)) {
            return true;
        }
        return false;
    }
}
