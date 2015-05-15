package com.tngtech.java.junit.dataprovider.internal.convert;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

public class ObjectArrayConverter {

    /**
     * TODO javadoc
     *
     * @param data array of arguments for test method
     * @param isVarArgs determines whether test method has a varargs parameter
     * @param parameterTypes target types of parameters to which corresponding values in regex-separated {@code data}
     *            should be converted
     * @return TODO
     */
    public Object[] convert(Object[] data, boolean isVarArgs, Class<?>[] parameterTypes) { // TODO test and integrate into any other / consolidate with other methods?
        Object[] result = new Object[parameterTypes.length];

        if (isVarArgs) {
            int nonVarArgParametersLength = parameterTypes.length - 1;
            for (int idx = 0; idx < nonVarArgParametersLength; idx++) {
                result[idx] = data[idx];
            }

            // data.length == parameterTypes.length &&
            if (data.length > 0
                    && data[data.length - 1].getClass().isArray()
                    && data[data.length - 1].getClass().getComponentType() == parameterTypes[nonVarArgParametersLength]
                            .getComponentType()) {
                result[nonVarArgParametersLength] = data[data.length - 1];
            } else {
                int varArgArrayLength = data.length - parameterTypes.length + 1;
                Object varArgArray = Array.newInstance(parameterTypes[nonVarArgParametersLength].getComponentType(),
                        varArgArrayLength);
                for (int idx = nonVarArgParametersLength; idx < data.length; idx++) {
                    Array.set(varArgArray, idx - nonVarArgParametersLength, data[idx]);
                }
                result[nonVarArgParametersLength] = varArgArray;
            }

        } else {
            result = data;
        }
        checkIfArgumentsMatchParameterTypes(result, parameterTypes);
        return result;
    }

    /**
     * Checks if the types of the given list of {@code arguments} matches the given test methods {@code parameterTypes}
     * and throws an {@link Error} if not.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param arguments the arguments to be used for each test method to be executed
     * @param parameterTypes test method parameter types (from {@link Method#getParameterTypes()})
     * @throws NullPointerException iif given {@code parameterTypes} or {@code settings} are {@code null}
     * @throws IllegalArgumentException iif test methods parameter types does not match the given {@code arguments}
     */
    void checkIfArgumentsMatchParameterTypes(Object[] arguments, Class<?>[] parameterTypes) {
        if (arguments == null) {
            throw new NullPointerException("arguments must not be null");
        }
        if (parameterTypes == null) {
            throw new NullPointerException("testMethod must not be null");
        }

        if (parameterTypes.length != arguments.length) {
            throw new IllegalArgumentException(String.format(
                    "Expected %s arguments for test method but got %s parameters.", parameterTypes.length,
                    arguments.length));
        }
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
