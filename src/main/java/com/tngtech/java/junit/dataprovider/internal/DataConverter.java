package com.tngtech.java.junit.dataprovider.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.tngtech.java.junit.dataprovider.DataProvider;

/**
 * Internal class to convert some data to its corresponding parameters.
 */
public class DataConverter {

    /**
     * Settings to be used to convert data to {@link List}{@code <}{@link Object}{@code >} by
     * {@link #convert(Object, Class[], Settings)}
     */
    public static class Settings {
        public final String splitBy;
        public final boolean convertNulls;
        public final boolean trimValues;

        public Settings(DataProvider dataProvider) {
            if (dataProvider == null) {
                throw new NullPointerException("dataProvider must not be null");
            }
            this.splitBy = dataProvider.splitBy();
            this.convertNulls = dataProvider.convertNulls();
            this.trimValues = dataProvider.trimValues();
        }
    }

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
     * @param settings to be used to convert given {@code data}
     * @return converted data as {@link List}{@code <}{@link Object}{@code []>} with the required {@code parameterTypes}
     * @throws NullPointerException iif given {@code parameterTypes} or {@code settings} are {@code null}
     * @throws IllegalArgumentException iif given {@code parameterTypes} is empty
     * @throws ClassCastException iif {@code data} is not a compatible type
     */
    public List<Object[]> convert(Object data, Class<?>[] parameterTypes, Settings settings) {
        if (parameterTypes == null) {
            throw new NullPointerException("parameterTypes must not be null");
        }
        if (settings == null) {
            throw new NullPointerException("settings must not be null");
        }
        if (parameterTypes.length == 0) {
            throw new IllegalArgumentException("parameterTypes must not be empty");
        }

        List<Object[]> result = new ArrayList<Object[]>();
        if (data instanceof Object[][]) {
            for (Object[] parameters : (Object[][]) data) {
                result.add(parameters);
            }

        } else if (data instanceof String[]) {
            int idx = 0;
            for (String paramString : (String[]) data) {
                result.add(getParametersFor(paramString, parameterTypes, settings, idx++));
            }

        } else if (data instanceof List) {
            @SuppressWarnings("unchecked")
            List<List<Object>> lists = (List<List<Object>>) data;
            for (List<Object> parameters : lists) {
                result.add(parameters.toArray());
            }

        } else {
            throw new ClassCastException(String.format(
                    "Cannot cast to either Object[][], String[], or List<List<Object>> because data was: %s", data));
        }
        return result;
    }

    /**
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param data regex-separated {@link String} of parameters for test method
     * @param parameterTypes target types of parameters to which corresponding value in regex-separated {@code data}
     *            should be converted
     * @param settings to be used to convert given {@code data}
     * @param rowIdx index of current {@code data} for better error messages
     * @return split, trimmed and converted {@code Object[]} of supplied regex-separated {@code data}
     */
    Object[] getParametersFor(String data, Class<?>[] parameterTypes, Settings settings, int rowIdx) {
        if (data == null) {
            return new Object[] { null };
        }

        Object[] result = new Object[parameterTypes.length];

        String[] splitData = splitBy(data, settings.splitBy);
        if (parameterTypes.length != splitData.length) {
            throw new Error(String.format("Test method expected %d parameters but got %d from @DataProvider row %d",
                    parameterTypes.length, splitData.length, rowIdx));
        }
        for (int idx = 0; idx < splitData.length; idx++) {
            String toConvert = (settings.trimValues) ? splitData[idx].trim() : splitData[idx];
            if (settings.convertNulls && "null".equals(toConvert)) {
                result[idx] = null;
            } else {
                result[idx] = convertValue(toConvert, parameterTypes[idx]);
            }
        }
        return result;
    }

    private String[] splitBy(String data, String regex) {
        // add trailing null terminator that split for "regex" ending data works properly
        String[] splitData = (data + "\0").split(regex);

        // remove added null terminator
        int lastItemIdx = splitData.length - 1;
        splitData[lastItemIdx] = splitData[lastItemIdx].substring(0, splitData[lastItemIdx].length() - 1);

        return splitData;
    }

    private Object convertValue(String str, Class<?> targetType) {
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
            return convertToChar(str, targetType);
        }
        if (short.class.equals(targetType) || Short.class.equals(targetType)) {
            return Short.valueOf(str);
        }
        if (int.class.equals(targetType) || Integer.class.equals(targetType)) {
            return Integer.valueOf(str);
        }
        if (long.class.equals(targetType) || Long.class.equals(targetType)) {
            return convertToLong(str);
        }
        if (float.class.equals(targetType) || Float.class.equals(targetType)) {
            return Float.valueOf(str);
        }
        if (double.class.equals(targetType) || Double.class.equals(targetType)) {
            return Double.valueOf(str);
        }
        if (targetType.isEnum()) {
            return convertToEnumValue(str, targetType);
        }

        Object result = tryConvertUsingSingleStringParamConstructor(str, targetType);
        if (result != null) {
            return result;
        }

        throw new Error("'" + targetType.getSimpleName() + "' is not supported as parameter type of test methods"
                + ". Supported types are primitive types and their wrappers, case-sensitive 'Enum'"
                + " values, 'String's, and types having a single 'String' parameter constructor.");
    }

    private Object convertToChar(String str, Class<?> charType) throws Error {
        if (str.length() == 1) {
            return str.charAt(0);
        }
        throw new Error(String.format("'%s' cannot be converted to %s.", str, charType.getSimpleName()));
    }

    private Object convertToLong(String str) {
        String longStr = str;
        if (longStr.endsWith("l")) {
            longStr = longStr.substring(0, longStr.length() - 1);
        }
        return Long.valueOf(longStr);
    }

    private Object convertToEnumValue(String str, Class<?> enumType) throws Error {
        try {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Enum result = Enum.valueOf((Class<Enum>) enumType, str);
            return result;

        } catch (IllegalArgumentException e) {
            throw new Error(String.format("'%s' is not a valid value of enum %s. Please be aware of case sensitivity.",
                    str, enumType.getSimpleName()));
        }
    }

    private Object tryConvertUsingSingleStringParamConstructor(String str, Class<?> targetType) {
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
        return null;
    }
}
