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

import static com.tngtech.junit.dataprovider.Preconditions.checkArgument;
import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;

import java.lang.reflect.Method;

public abstract class AbstractObjectConverter<V> {

    /**
     * Converts the given {@code data} to its corresponding arguments using the provided information. Additionally
     * checks the arguments against the given parameter types before returning.
     *
     * @param data array of arguments for test method
     * @param isVarargs determines whether test method has a varargs parameter
     * @param parameterTypes target types of parameters
     * @return {@code Object[]} which is converted for varargs support and checked against {@code parameterTypes}
     * @throws IllegalArgumentException if and only if the data does not fit somehow
     */
    public abstract Object[] convert(V data, boolean isVarargs, Class<?>[] parameterTypes);

    /**
     * Checks if the types of the given {@code arguments} matches the given test methods {@code parameterTypes} and
     * throws an {@link IllegalArgumentException} if not.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param arguments the arguments to be used for each test method to be executed
     * @param parameterTypes test method parameter types (from {@link Method#getParameterTypes()})
     * @throws NullPointerException if and only if given {@code arguments} or {@code parameterTypes} are {@code null}
     * @throws IllegalArgumentException if and only if the {@code arguments} does not match the given parameter types
     */
    protected void checkIfArgumentsMatchParameterTypes(Object[] arguments, Class<?>[] parameterTypes) {
        checkNotNull(arguments, "'arguments' must not be null");
        checkNotNull(parameterTypes, "'testMethod' must not be null");
        checkArgument(parameterTypes.length >= arguments.length,
                "Expected at most %d arguments for test method but got %d.", parameterTypes.length, arguments.length);

        for (int idx = 0; idx < arguments.length; idx++) {
            Object object = arguments[idx];
            if (object != null) {
                Class<?> paramType = parameterTypes[idx];
                if (!paramType.isInstance(object) && !isWrappedInstance(paramType, object)
                        && !isWideningConversion(paramType, object)) {
                    throw new IllegalArgumentException(
                            String.format("Parameter number %d is of type '%s' but argument given is '%s' of type '%s'",
                                    idx, paramType.getSimpleName(), object, object.getClass().getSimpleName()));
                }
            }
        }
    }

    private boolean isWrappedInstance(Class<?> clazz, Object object) {
        return (boolean.class.equals(clazz) && object instanceof Boolean)
                || (byte.class.equals(clazz) && object instanceof Byte)
                || (char.class.equals(clazz) && object instanceof Character)
                || (double.class.equals(clazz) && object instanceof Double)
                || (float.class.equals(clazz) && object instanceof Float)
                || (int.class.equals(clazz) && object instanceof Integer)
                || (long.class.equals(clazz) && object instanceof Long)
                || (short.class.equals(clazz) && object instanceof Short)
                || (void.class.equals(clazz) && object instanceof Void);
    }

    private boolean isWideningConversion(Class<?> clazz, Object object) {
        // byte to short, int, long, float, or double
        if ((short.class.equals(clazz) || int.class.equals(clazz) || long.class.equals(clazz)
                || float.class.equals(clazz) || double.class.equals(clazz))
                && object instanceof Byte) {
            return true;
        }

        // short or char to int, long, float, or double
        if ((int.class.equals(clazz) || long.class.equals(clazz) || float.class.equals(clazz) || double.class
                .equals(clazz)) && (object instanceof Short || object instanceof Character)) {
            return true;
        }
        // int to long, float, or double
        if ((long.class.equals(clazz) || float.class.equals(clazz) || double.class.equals(clazz))
                && object instanceof Integer) {
            return true;
        }
        // long to float or double
        if ((float.class.equals(clazz) || double.class.equals(clazz)) && object instanceof Long) {
            return true;
        }
        // float to double
        if ((double.class.equals(clazz)) && object instanceof Float) {
            return true;
        }
        return false;
    }
}
