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
     * Returns {@code true} iif this {@link DataConverter} can convert the given {@code type}. Currently supported
     * {@code type}s:
     * <ul>
     * <li>Object[][]</li>
     * <li>List&lt;List&lt;Object&gt;&gt;</li>
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
     * @param dataProvider containing settings which should be used to convert given {@code data}
     * @return converted data as {@link List}{@code <}{@link Object}{@code []>} with the required {@code parameterTypes}
     * @throws NullPointerException iif given {@code parameterTypes} or {@code settings} are {@code null}
     * @throws IllegalArgumentException iif given {@code parameterTypes} is empty
     * @throws ClassCastException iif {@code data} is not a compatible type
     */
    public List<Object[]> convert(Object data, Class<?>[] parameterTypes, DataProvider dataProvider) {
        if (parameterTypes == null) {
            throw new NullPointerException("parameterTypes must not be null");
        }
        if (dataProvider == null) {
            throw new NullPointerException("dataProvider must not be null");
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
                result.add(getParametersFor(paramString, parameterTypes, dataProvider, idx++));
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
     * Checks if the types of the given list of {@code arguments} matches the given test methods {@code parameterTypes}
     * and throws an {@link Error} if not.
     *
     * @param arguments the arguments to be used for each test method to be executed
     * @param parameterTypes test method parameter types (from {@link Method#getParameterTypes()})
     * @throws NullPointerException iif given {@code parameterTypes} or {@code settings} are {@code null}
     * @throws IllegalArgumentException iif test methods parameter types does not match the given {@code arguments}
     */
    public void checkIfArgumentsMatchParameterTypes(List<Object[]> arguments, Class<?>[] parameterTypes) {
        if (arguments == null) {
            throw new NullPointerException("arguments must not be null");
        }
        if (parameterTypes == null) {
            throw new NullPointerException("testMethod must not be null");
        }

        for (Object[] objects : arguments) {
            if (parameterTypes.length != objects.length) {
                throw new IllegalArgumentException(String.format(
                        "Expected %s arguments for test method but got %s parameters.", parameterTypes.length,
                        objects.length));
            }
            for (int idx = 0; idx < objects.length; idx++) {
                Object object = objects[idx];
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
    }

    /**
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param data regex-separated {@link String} of parameters for test method
     * @param parameterTypes target types of parameters to which corresponding values in regex-separated {@code data}
     *            should be converted
     * @param dataProvider containing settings which should be used to convert given {@code data}
     * @param rowIdx index of current {@code data} (row) for better error messages
     * @return split, trimmed and converted {@code Object[]} of supplied regex-separated {@code data}
     * @throws IllegalArgumentException iif count of split data and paramter types differs
     */
    Object[] getParametersFor(String data, Class<?>[] parameterTypes, DataProvider dataProvider, int rowIdx) {
        if (data == null) {
            return new Object[] { null };
        }

        String[] splitData = splitBy(data, dataProvider.splitBy());
        if (parameterTypes.length != splitData.length) {
            throw new IllegalArgumentException(String.format(
                    "Test method expected %d parameters but got %d from @DataProvider row %d", parameterTypes.length,
                    splitData.length, rowIdx));
        }

        Object[] result = new Object[parameterTypes.length];
        for (int idx = 0; idx < splitData.length; idx++) {
            String toConvert = (dataProvider.trimValues()) ? splitData[idx].trim() : splitData[idx];
            if (dataProvider.convertNulls() && "null".equals(toConvert)) {
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

        try {
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
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Cannot convert %s to %s", str, targetType.getSimpleName()));
        }

        if (targetType.isEnum()) {
            return convertToEnumValue(str, targetType);
        }

        Object result = tryConvertUsingSingleStringParamConstructor(str, targetType);
        if (result != null) {
            return result;
        }

        throw new IllegalArgumentException("'" + targetType.getSimpleName()
                + "' is not supported as parameter type of test methods"
                + ". Supported types are primitive types and their wrappers, case-sensitive 'Enum'"
                + " values, 'String's, and types having a single 'String' parameter constructor.");
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
        if ((double.class.equals(clazz)) && Double.class.isInstance(object)) {
            return true;
        }
        return false;
    }

    private Object convertToChar(String str, Class<?> charType) {
        if (str.length() == 1) {
            return str.charAt(0);
        }
        throw new IllegalArgumentException(String.format("'%s' cannot be converted to %s.", str,
                charType.getSimpleName()));
    }

    private Object convertToLong(String str) {
        String longStr = str;
        if (longStr.endsWith("l")) {
            longStr = longStr.substring(0, longStr.length() - 1);
        }
        return Long.valueOf(longStr);
    }

    private Object convertToEnumValue(String str, Class<?> enumType) {
        try {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Enum result = Enum.valueOf((Class<Enum>) enumType, str);
            return result;

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format(
                    "'%s' is not a valid value of enum %s. Please be aware of case sensitivity.", str,
                    enumType.getSimpleName()));
        }
    }

    private Object tryConvertUsingSingleStringParamConstructor(String str, Class<?> targetType) {
        for (Constructor<?> constructor : targetType.getConstructors()) {
            if (constructor.getParameterTypes().length == 1 && String.class.equals(constructor.getParameterTypes()[0])) {
                try {
                    return constructor.newInstance(str);

                } catch (Exception e) {
                    throw new IllegalArgumentException(String.format(
                            "Tried to invoke '%s' for argument '%s'. Exception: %s", constructor, str, e.getMessage()),
                            e);
                }
            }
        }
        return null;
    }
}
