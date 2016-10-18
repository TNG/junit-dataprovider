package com.tngtech.java.junit.dataprovider.internal.convert;

import static com.tngtech.java.junit.dataprovider.DataProvider.NULL;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import com.tngtech.java.junit.dataprovider.DataProvider;

public class StringConverter {

    protected static final Object OBJECT_NO_CONVERSION = new Object();

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

        return convert(splitData, isVarArgs, parameterTypes, dataProvider);
    }

    protected String[] splitBy(String data, String regex) {
        // add trailing null terminator that split for "regex" ending data works properly
        String[] splitData = (data + "\0").split(regex);

        // remove added null terminator
        int lastItemIdx = splitData.length - 1;
        splitData[lastItemIdx] = splitData[lastItemIdx].substring(0, splitData[lastItemIdx].length() - 1);

        return splitData;
    }

    protected void checkArgumentsAndParameterCount(int argCount, int paramCount, boolean isVarArgs, int rowIdx) {
        if ((isVarArgs && paramCount - 1 > argCount) || (!isVarArgs && paramCount != argCount)) {
            throw new IllegalArgumentException(String.format("Test method expected %s %d parameters but got %d from @DataProvider row %d",
                    (isVarArgs) ? "at least " : "", paramCount - 1, argCount, rowIdx));
        }
    }

    private Object[] convert(String[] splitData, boolean isVarArgs, Class<?>[] parameterTypes, DataProvider dataProvider) {
        Object[] result = new Object[parameterTypes.length];

        int nonVarArgParametersLength = parameterTypes.length - ((isVarArgs) ? 1 : 0);
        for (int idx = 0; idx < nonVarArgParametersLength; idx++) {
            result[idx] = convertValue(splitData[idx], parameterTypes[idx], dataProvider);
        }

        if (isVarArgs) {
            Class<?> varArgComponentType = parameterTypes[nonVarArgParametersLength].getComponentType();

            Object varArgArray = Array.newInstance(varArgComponentType, splitData.length - parameterTypes.length + 1);
            for (int idx = nonVarArgParametersLength; idx < splitData.length; idx++) {
                Array.set(varArgArray, idx - nonVarArgParametersLength, convertValue(splitData[idx], varArgComponentType, dataProvider));
            }
            result[nonVarArgParametersLength] = varArgArray;
        }
        return result;
    }

    private Object convertValue(String data, Class<?> targetType, DataProvider dataProvider) {
        String str = (dataProvider.trimValues()) ? data.trim() : data;
        if (dataProvider.convertNulls() && NULL.equals(str)) {
            return null;
        }

        Object tmp = customConvertValue(str, targetType, dataProvider);
        if (tmp != OBJECT_NO_CONVERSION) {
            return tmp;
        }

        if (String.class.equals(targetType)) {
            return str;
        }

        Object primaryOrWrapper = convertPrimaryOrWrapper(str, targetType);
        if (primaryOrWrapper != null) {
            return primaryOrWrapper;
        }

        if (targetType.isEnum()) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Class<Enum> enumType = (Class<Enum>) targetType;
            return convertToEnumValue(str, enumType, dataProvider.ignoreEnumCase());
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

    /**
     * This method purely exists as potential extension point by overriding it.
     *
     * @param str value to be converted
     * @param targetType target type into which value should be converted
     * @param dataProvider containing settings which should be used to convert given {@code data}
     * @return to target type converted {@link String} or {@link #OBJECT_NO_CONVERSION} if no conversion was applied.
     *         Later will imply that normal conversions try to apply.
     */
    protected Object customConvertValue(String str, Class<?> targetType, DataProvider dataProvider) {
        return OBJECT_NO_CONVERSION;
    }

    protected Object convertPrimaryOrWrapper(String str, Class<?> targetType) {
        try {
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
                throw new IllegalArgumentException(String.format("'%s' cannot be converted to %s.", str, targetType.getSimpleName()));
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
            throw new IllegalArgumentException(String.format("Cannot convert %s to %s", str, targetType.getSimpleName()));
        }
        return null;
    }

    protected Object convertToLong(String str) {
        String longStr = str;
        if (longStr.endsWith("l")) {
            longStr = longStr.substring(0, longStr.length() - 1);
        }
        return Long.valueOf(longStr);
    }

    @SuppressWarnings("rawtypes")
    protected Object convertToEnumValue(String str, Class<Enum> enumType, boolean ignoreEnumCase) {
        String errorMessage = "'%s' is not a valid value of enum %s.";
        if (ignoreEnumCase) {
            for (Enum<?> enumConstant : enumType.getEnumConstants()) {
                if (str.equalsIgnoreCase(enumConstant.name())) {
                    return enumConstant;
                }
            }
        } else {
            try {
                @SuppressWarnings("unchecked")
                Enum result = Enum.valueOf(enumType, str);
                return result;

            } catch (IllegalArgumentException e) {
                errorMessage += " Please be aware of case sensitivity or use 'ignoreEnumCase' of @"
                        + DataProvider.class.getSimpleName() + ".";
            }
        }
        throw new IllegalArgumentException(String.format(errorMessage, str, enumType.getSimpleName()));
    }

    protected Object tryConvertUsingSingleStringParamConstructor(String str, Class<?> targetType) {
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
