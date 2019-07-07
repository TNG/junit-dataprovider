/*
 * Copyright 2019 TNG Technology Consulting GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tngtech.junit.dataprovider.convert;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

public class StringConverter {

    protected static final Object OBJECT_NO_CONVERSION = new Object();

    /**
     * Converts the given {@code data} to its corresponding arguments using the given {@code parameterTypes} and other
     * provided information.
     *
     * @param data regex-separated {@link String} of arguments for test method
     * @param isVarargs determines whether test method has a varargs parameter
     * @param parameterTypes target types of parameters to which corresponding values in regex-separated {@code data}
     *            should be converted
     * @param context containing settings which should be used to convert given {@code data}
     * @param rowIdx index of current {@code data} (row) for better error messages
     * @return split, trimmed and converted {@code Object[]} of supplied regex-separated {@code data}
     * @throws IllegalArgumentException if and only if count of split data and parameter types does not match or
     *             argument cannot be converted to required type
     */
    public Object[] convert(String data, boolean isVarargs, Class<?>[] parameterTypes, ConverterContext context, int rowIdx) {
        if (data == null) {
            return new Object[] { null };
        }
        if (parameterTypes.length == 1) {
            if (isVarargs) {
                if (data.isEmpty()) {
                    return new Object[] { Array.newInstance(parameterTypes[0].getComponentType(), 0) };
                }
            } else {
                return new Object[] { convertValue(data, parameterTypes[0], context) };
            }
        }

        String[] splitData = splitBy(data, context.getSplitBy());

        checkArgumentsAndParameterCount(splitData.length, parameterTypes.length, isVarargs, rowIdx);

        return convert(splitData, isVarargs, parameterTypes, context);
    }

    protected String[] splitBy(String data, String regex) {
        // add trailing null terminator that split for "regex" ending data works properly
        String[] splitData = (data + "\0").split(regex);

        // remove added null terminator
        int lastItemIdx = splitData.length - 1;
        splitData[lastItemIdx] = splitData[lastItemIdx].substring(0, splitData[lastItemIdx].length() - 1);

        return splitData;
    }

    protected void checkArgumentsAndParameterCount(int argCount, int paramCount, boolean isVarargs, int rowIdx) {
        if ((isVarargs && paramCount - 1 > argCount) || (!isVarargs && paramCount < argCount)) {
            throw new IllegalArgumentException(
                    String.format("%sest method has %d parameters but got %s%d arguments in row %d",
                            (isVarargs) ? "Varargs t" : "T", paramCount, (isVarargs) ? "only " : "", argCount, rowIdx));
        }
    }

    private Object[] convert(String[] splitData, boolean isVarargs, Class<?>[] parameterTypes, ConverterContext context) {
        Object[] result= new Object[(isVarargs) ? parameterTypes.length : splitData.length];

        int nonVarargParametersLength = (isVarargs) ? parameterTypes.length - 1 : splitData.length;
        for (int idx = 0; idx < nonVarargParametersLength; idx++) {
            result[idx] = convertValue(splitData[idx], parameterTypes[idx], context);
        }

        if (isVarargs) {
            Class<?> varargComponentType = parameterTypes[nonVarargParametersLength].getComponentType();

            Object varargArray = Array.newInstance(varargComponentType, splitData.length - parameterTypes.length + 1);
            for (int idx = nonVarargParametersLength; idx < splitData.length; idx++) {
                Array.set(varargArray, idx - nonVarargParametersLength,
                        convertValue(splitData[idx], varargComponentType, context));
            }
            result[nonVarargParametersLength] = varargArray;
        }
        return result;
    }

    private Object convertValue(String data, Class<?> targetType, ConverterContext context) {
        String str = (context.isTrimValues()) ? data.trim() : data;
        if (context.isConvertNulls() && ConverterContext.NULL.equals(str)) {
            return null;
        }

        Object tmp = customConvertValue(str, targetType, context);
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
            return convertToEnumValue(str, enumType, context.isIgnoreEnumCase());
        }

        if (Class.class.equals(targetType)) {
            try {
                return Class.forName(str);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        String.format("Unable to instantiate '%s' for '%s'", targetType.getSimpleName(), str), e);
            }
        }

        Object result = tryConvertUsingSingleStringParamConstructor(str, targetType);
        if (result != null) {
            return result;
        }

        throw new IllegalArgumentException(String.format(
                "Type '%s' is not supported as parameter type of test methods. Supported types are primitive types and their wrappers, 'Enum' values, 'String's, and types having a single 'String' parameter constructor.",
                targetType.getSimpleName()));
    }

    /**
     * This method purely exists as potential extension point by overriding it.
     *
     * @param str value to be converted
     * @param targetType target type into which value should be converted
     * @param context containing settings which should be used to convert given {@code data}
     * @return to target type converted {@link String} or {@link #OBJECT_NO_CONVERSION} if no conversion was applied.
     *         Later will imply that normal conversions try to apply.
     */
    @SuppressWarnings("unused")
    protected Object customConvertValue(String str, Class<?> targetType, ConverterContext context) {
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
                throw new IllegalArgumentException(
                        String.format("'%s' cannot be converted to type '%s'.", str, targetType.getSimpleName()));
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
                    String.format("Cannot convert '%s' to type '%s'", str, targetType.getSimpleName()), e);
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
        String errorMessage = "'%s' is not a valid value of enum '%s'.";
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
                errorMessage += " Please be aware of case sensitivity or use 'ignoreEnumCase'. Error was: " + e.getMessage();
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
                    throw new IllegalArgumentException(
                            String.format("Tried to invoke '%s' for argument '%s'. Exception was: %s", constructor, str,
                                    e.getMessage()),
                            e);
                }
            }
        }
        return null;
    }
}
