package com.tngtech.junit.dataprovider.convert;

import java.lang.reflect.Array;

public class ObjectArrayConverter extends AbstractObjectConverter<Object[]> {

    /**
     * {@inheritDoc}
     *
     * @param data array of arguments for test method
     * @param isVarargs determines whether test method has a varargs parameter
     * @param parameterTypes target types of parameters
     * @return {@code Object[]} which is converted for varargs support and checked against {@code parameterTypes}
     * @throws IllegalArgumentException if and only if the data does not fit the varargs array component type
     */
    @Override
    public Object[] convert(Object[] data, boolean isVarargs, Class<?>[] parameterTypes) {
        Object[] result;
        if (isVarargs) {
            result = new Object[parameterTypes.length];

            int lastArgIdx = parameterTypes.length - 1;
            for (int idx = 0; idx < lastArgIdx; idx++) {
                result[idx] = data[idx];
            }
            result[lastArgIdx] = convertVararg(data, parameterTypes[lastArgIdx].getComponentType(), lastArgIdx);

        } else {
            result = data;
        }
        checkIfArgumentsMatchParameterTypes(result, parameterTypes);
        return result;
    }

    private Object convertVararg(Object[] data, Class<?> varargComponentType, int nonVarargParameterCount) {
        if (data.length > 0) {
            Object date = data[data.length - 1];
            Class<?> lastArgType = date != null ? date.getClass() : null;
            if (lastArgType != null && lastArgType.isArray() && lastArgType.getComponentType() == varargComponentType) {
                return date;
            }
        }

        Object varargArray = Array.newInstance(varargComponentType, data.length - nonVarargParameterCount);
        for (int idx = nonVarargParameterCount; idx < data.length; idx++) {
            Array.set(varargArray, idx - nonVarargParameterCount, data[idx]);
        }
        return varargArray;
    }
}
