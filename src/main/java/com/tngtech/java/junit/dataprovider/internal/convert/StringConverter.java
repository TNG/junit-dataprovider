package com.tngtech.java.junit.dataprovider.internal.convert;

import static com.tngtech.java.junit.dataprovider.DataProvider.NULL;

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
    public Object[] convert(String data, boolean isVarArgs, Class<?>[] parameterTypes, DataProvider dataProvider,
            int rowIdx) {
        if (data == null) {
            return new Object[] { null };
        }
        if (parameterTypes.length == 1) {
            if (isVarArgs) {
                if (data.isEmpty()) {
                    return new Object[] { Array.newInstance(parameterTypes[0].getComponentType(), 0) };
                }
            } else {
                return new Object[] { convertValue(data, parameterTypes[0], dataProvider) };
            }
        }

        String[] splitData = splitBy(data, dataProvider.splitBy());

        checkArgumentsAndParameterCount(splitData.length, parameterTypes.length, isVarArgs, rowIdx);

        Object[] result = new Object[parameterTypes.length];

        int nonVarArgParametersLength = parameterTypes.length - ((isVarArgs) ? 1 : 0);
        for (int idx = 0; idx < nonVarArgParametersLength; idx++) {
            result[idx] = convertValue(splitData[idx], parameterTypes[idx], dataProvider);
        }

        if (isVarArgs) {
            Class<?> varArgComponentType = parameterTypes[nonVarArgParametersLength].getComponentType();

            Object varArgArray = Array.newInstance(varArgComponentType, splitData.length - parameterTypes.length + 1);
            for (int idx = nonVarArgParametersLength; idx < splitData.length; idx++) {
                Array.set(varArgArray, idx - nonVarArgParametersLength,
                        convertValue(splitData[idx], varArgComponentType, dataProvider));
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

    private void checkArgumentsAndParameterCount(int argCount, int paramCount, boolean isVarArgs, int rowIdx) {
        if ((isVarArgs && paramCount - 1 > argCount) || (!isVarArgs && paramCount != argCount)) {
            throw new IllegalArgumentException(String.format(
                    "Test method expected %s %d parameters but got %d from @DataProvider row %d",
                    (isVarArgs) ? "at least " : "", paramCount - 1, argCount, rowIdx));
        }
    }

    private Object convertValue(String data, Class<?> targetType, DataProvider dataProvider) {
        String toConvert = (dataProvider.trimValues()) ? data.trim() : data;
        if (dataProvider.convertNulls() && NULL.equals(toConvert)) {
            return null;
        }
        return convertValue(toConvert, targetType);
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

        if (Class.class.equals(targetType)) {
            try {
                return Class.forName(str);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        String.format("Unable to instantiate %s for '%s'", targetType.getSimpleName(), str), e);
            }
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
