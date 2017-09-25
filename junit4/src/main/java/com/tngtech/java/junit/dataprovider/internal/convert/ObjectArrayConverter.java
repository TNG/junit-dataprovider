package com.tngtech.java.junit.dataprovider.internal.convert;

import java.lang.reflect.Array;

public class ObjectArrayConverter extends AbstractObjectConverter<Object[]> {

    /**
     * {@inheritDoc}
     *
     * @param data array of arguments for test method
     * @param isVarArgs determines whether test method has a varargs parameter
     * @param parameterTypes target types of parameters
     * @return {@code Object[]} which is converted for varargs support and checked against {@code parameterTypes}
     * @throws IllegalArgumentException iif the data does not fit the varargs array component type
     */
    @Override
    public Object[] convert(Object[] data, boolean isVarArgs, Class<?>[] parameterTypes) {
        Object[] result = new Object[parameterTypes.length];

        int lastArgIdx = parameterTypes.length - 1;
        for (int idx = 0; idx < lastArgIdx; idx++) {
            result[idx] = data[idx];
        }

        if (isVarArgs) {
            result[lastArgIdx] = convertVarArgArgument(data, parameterTypes[lastArgIdx].getComponentType(), lastArgIdx);
        } else {
            result[lastArgIdx] = data[data.length - 1];
        }

        checkIfArgumentsMatchParameterTypes(result, parameterTypes);
        return result;
    }

    private Object convertVarArgArgument(Object[] data, Class<?> varArgComponentType, int nonVarArgParameters) {
        if (data.length > 0) {
            Object date = data[data.length - 1];
            Class<?> lastArgType = date != null ? date.getClass() : null;
            if (lastArgType != null && lastArgType.isArray() && lastArgType.getComponentType() == varArgComponentType) {
                return date;
            }
        }

        Object varArgArray = Array.newInstance(varArgComponentType, data.length - nonVarArgParameters);
        for (int idx = nonVarArgParameters; idx < data.length; idx++) {
            Array.set(varArgArray, idx - nonVarArgParameters, data[idx]);
        }
        return varArgArray;
    }
}
