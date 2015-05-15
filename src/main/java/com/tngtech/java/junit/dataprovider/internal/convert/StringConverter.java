package com.tngtech.java.junit.dataprovider.internal.convert;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import com.tngtech.java.junit.dataprovider.DataProvider;

public class StringConverter {

    /**
     * Converts the given {@code data} to its corresponding arguments using the given {@code parameterTypes} and other
     * provided information.
     *
     * @param data regex-separated {@link String} of parameters for test method
     * @param isVarArgs determines whether test method has a varargs parameter
     * @param parameterTypes target types of parameters to which corresponding values in regex-separated {@code data}
     *            should be converted
     * @param dataProvider containing settings which should be used to convert given {@code data}
     * @param rowIdx index of current {@code data} (row) for better error messages
     * @return split, trimmed and converted {@code Object[]} of supplied regex-separated {@code data}
     * @throws IllegalArgumentException iif count of split data and parameter types does not match or argument cannot be
     *             converted to required type
     */
    public Object[] convert(String data, boolean isVarArgs, Class<?>[] parameterTypes,
            DataProvider dataProvider, int rowIdx) {
        if (data == null) {
            return new Object[] { null };
        }
        String[] splitData = splitBy(data, dataProvider.splitBy());

        if ((isVarArgs && parameterTypes.length - 1 > splitData.length)
                || (!isVarArgs && parameterTypes.length != splitData.length)) {
            throw new IllegalArgumentException(String.format(
                    "Test method expected %s %d parameters but got %d from @DataProvider row %d",
                    (isVarArgs) ? "at least " : "", parameterTypes.length - 1, splitData.length, rowIdx));
        }
        Object[] result = new Object[parameterTypes.length];

        int nonVarArgParametersLength = parameterTypes.length - ((isVarArgs) ? 1 : 0); // TODO this is still a bit duplicate code ...
        for (int idx = 0; idx < nonVarArgParametersLength; idx++) {
            String toConvert = (dataProvider.trimValues()) ? splitData[idx].trim() : splitData[idx];
            if (dataProvider.convertNulls() && "null".equals(toConvert)) {
                result[idx] = null;
            } else {
                result[idx] = convertValue(toConvert, parameterTypes[idx]);
            }
        }

        if (isVarArgs) { // TODO maybe integrate into above loop?
            Object varArgArray;
            if (splitData.length == 1
                    && ((dataProvider.trimValues()) ? splitData[splitData.length - 1].trim()
                            : splitData[splitData.length - 1]).isEmpty()) {
                varArgArray = Array.newInstance(parameterTypes[nonVarArgParametersLength].getComponentType(), 0);
            } else {
                int varArgArrayLength = splitData.length - parameterTypes.length + 1;
                varArgArray = Array.newInstance(parameterTypes[nonVarArgParametersLength].getComponentType(),
                        varArgArrayLength);
                for (int idx = nonVarArgParametersLength; idx < splitData.length; idx++) {
                    String toConvert = (dataProvider.trimValues()) ? splitData[idx].trim() : splitData[idx];
                    if (dataProvider.convertNulls() && "null".equals(toConvert)) {
                        Array.set(varArgArray, idx - nonVarArgParametersLength, null);
                    } else {
                        Array.set(varArgArray, idx - nonVarArgParametersLength,
                                convertValue(toConvert, parameterTypes[nonVarArgParametersLength].getComponentType()));
                    }
                }
            }
            result[nonVarArgParametersLength] = varArgArray;
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
