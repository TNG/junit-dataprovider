package com.tngtech.java.junit.dataprovider.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal class to convert some data to its corresponding parameters.
 */
public class DataConverter {

    /**
     * Returns {@code true} iif this {@link DataConverter} can convert the given {@code type}. Currently supported
     * {@code type}s:
     * <ul>
     * <li>Object[][]</li>
     * <li>List<List<Object>></li>
     * <li>String[]</li>
     * </ul>
     *
     * @param type to be checked for convertibility (use either {@link Method#getGenericReturnType()},
     *            {@link Method#getReturnType()}, or simple {@link Class} if possible)
     * @return {@code true} iif given {@code type} can be converted.
     */
    public boolean canConvert(Type type) {
        if (type instanceof Class) {
            return Object[][].class.equals(type) || String[].class.equals(type);
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (List.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                if (parameterizedType.getActualTypeArguments().length == 1
                        && parameterizedType.getActualTypeArguments()[0] instanceof ParameterizedType) {
                    ParameterizedType innerType = (ParameterizedType) parameterizedType.getActualTypeArguments()[0];
                    return List.class.isAssignableFrom((Class<?>) innerType.getRawType());
                }
            }
        }
        return false;

    }

    /**
     * Converts the given {@link Object} to a {@link List} of {@link Object}{@code []} with {@link Class}es correspond
     * to given {@code parameterTypes}.
     * <p>
     * For compatible types, see {@link #canConvert(Type)}.
     *
     * @param data to be converted
     * @param parameterTypes required types for {@code data}
     * @return converted data as {@link List}{@code <}{@link Object}{@code []>} with the required {@code parameterTypes}
     * @throws NullPointerException iif given {@code parameterTypes} is {@code null}
     * @throws IllegalArgumentException iif given {@code parameterTypes} is empty
     * @throws ClassCastException iif {@code data} is not a compatible type
     */
    public List<Object[]> convert(Object data, Class<?>[] parameterTypes) {
        if (parameterTypes == null) {
            throw new NullPointerException("parameterTypes must not be null");
        }
        if (parameterTypes.length == 0) {
            throw new IllegalArgumentException("parameterTypes must not be empty");
        }

        List<Object[]> result = new ArrayList<Object[]>();

        if (data instanceof Object[][]) {
            for (Object[] parameters : (Object[][]) data) {
                result.add(parameters);
            }
            return result;

        } else if (data instanceof String[]) {
            int idx = 0;
            for (String paramString : (String[]) data) {
                result.add(getParametersFor(paramString, parameterTypes, idx++));
            }
            return result;

        } else if (data instanceof List) {
            @SuppressWarnings("unchecked")
            List<List<Object>> lists = (List<List<Object>>) data;
            for (List<Object> parameters : lists) {
                result.add(parameters.toArray());
            }
            return result;
        }
        throw new ClassCastException(String.format(
                "Cannot cast to either Object[][], String[], or List<List<Object>> because data was: %s", data));
    }

    /**
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param data comma separated {@link String} of parameters for test method
     * @param parameterTypes target types of parameters to which corresponding value in comma separated {@code data}
     *            should be converted
     * @param rowIdx index of current {@code data} for better error messages
     * @return split, trimmed and converted {@code Object[]} of supplied comma separated {@code data}
     */
    Object[] getParametersFor(String data, Class<?>[] parameterTypes, int rowIdx) {
        Object[] result = new Object[parameterTypes.length];

        String[] splitData = (data + " ").split(","); // add trailing whitespace that split for comma ended data works
        if (parameterTypes.length != splitData.length) {
            throw new Error(String.format("Test method expected %d parameters but got %d from @DataProvider row %d",
                    parameterTypes.length, splitData.length, rowIdx));
        }

        for (int idx = 0; idx < splitData.length; idx++) {
            result[idx] = convertValue(splitData[idx].trim(), parameterTypes[idx]);
        }
        return result;
    }

    private Object convertValue(String str, Class<?> targetType) {
        if ("null".equals(str)) {
            return null;
        }

        if (String.class.equals(targetType)) {
            return str;
        }

        if (boolean.class.equals(targetType) || Boolean.class.equals(targetType)) {
            return Boolean.valueOf(str);
        }
        if (byte.class.equals(targetType) || Byte.class.equals(targetType)) {
            return Byte.valueOf(str);
        }
        if (char.class.equals(targetType) || Character.class.equals(targetType)) {
            if (str.length() == 1) {
                return str.charAt(0);
            }
            throw new Error(String.format("'%s' cannot be converted to %s.", str, targetType.getSimpleName()));
        }
        if (short.class.equals(targetType) || Short.class.equals(targetType)) {
            return Short.valueOf(str);
        }
        if (int.class.equals(targetType) || Integer.class.equals(targetType)) {
            return Integer.valueOf(str);
        }
        if (long.class.equals(targetType) || Long.class.equals(targetType)) {
            String longStr = str;
            if (longStr.endsWith("l")) {
                longStr = longStr.substring(0, longStr.length() - 1);
            }
            return Long.valueOf(longStr);
        }
        if (float.class.equals(targetType) || Float.class.equals(targetType)) {
            return Float.valueOf(str);
        }
        if (double.class.equals(targetType) || Double.class.equals(targetType)) {
            return Double.valueOf(str);
        }

        if (targetType.isEnum()) {
            try {
                @SuppressWarnings({ "rawtypes", "unchecked" })
                Enum result = Enum.valueOf((Class<Enum>) targetType, str);
                return result;

            } catch (IllegalArgumentException e) {
                throw new Error(String.format(
                        "'%s' is not a valid value of enum %s. Please be aware of case sensitivity.", str,
                        targetType.getSimpleName()));
            }
        }

        for (Constructor<?> constructor : targetType.getConstructors()) {
            if (constructor.getParameterTypes().length == 1 && String.class.equals(constructor.getParameterTypes()[0])) {
                try {
                    return constructor.newInstance(str);

                } catch (Exception e) {
                    throw new Error(String.format("Tried to invoke '%s' for argument '%s'. Exception: %s", constructor,
                            str, e.getMessage()), e);
                }
            }
        }

        throw new Error("'" + targetType.getSimpleName() + "' is not supported as parameter type of test. Supported"
                + " types are primitive types, primitive wrapper types, case-sensitive 'Enum' values, 'String's"
                + ", and types having single-argument 'String' constructor.");
    }
}
