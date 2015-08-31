package com.tngtech.java.junit.dataprovider.internal;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.internal.convert.ObjectArrayConverter;
import com.tngtech.java.junit.dataprovider.internal.convert.StringConverter;

/**
 * Internal class to convert some data to its corresponding parameters.
 */
public class DataConverter {

    /**
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    final ObjectArrayConverter objectArrayConverter = new ObjectArrayConverter();

    /**
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    final StringConverter stringConverter;

    public DataConverter() {
    	this(new StringConverter());
    }
    
    public DataConverter(StringConverter converter) {
    	this.stringConverter = converter;
    }
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
     * @param isVarArgs determines whether test method has a varargs parameter
     * @param parameterTypes required types for {@code data}
     * @param dataProvider containing settings which should be used to convert given {@code data}
     * @return converted data as {@link List}{@code <}{@link Object}{@code []>} with the required {@code parameterTypes}
     * @throws NullPointerException iif given {@code parameterTypes} or {@code settings} are {@code null}
     * @throws IllegalArgumentException iif given {@code parameterTypes} is empty
     * @throws ClassCastException iif {@code data} is not a compatible type
     */
    public List<Object[]> convert(Object data, boolean isVarArgs, Class<?>[] parameterTypes, DataProvider dataProvider) {
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
            for (Object[] arguments : (Object[][]) data) {
                result.add(objectArrayConverter.convert(arguments, isVarArgs, parameterTypes));
            }

        } else if (data instanceof String[]) {
            int idx = 0;
            for (String argString : (String[]) data) {
                result.add(stringConverter.convert(argString, isVarArgs, parameterTypes, dataProvider, idx++));
            }

        } else if (data instanceof List) {
            @SuppressWarnings("unchecked")
            List<List<Object>> lists = (List<List<Object>>) data;
            for (List<Object> arguments : lists) {
                result.add(objectArrayConverter.convert(arguments.toArray(), isVarArgs, parameterTypes));
            }

        } else {
            throw new ClassCastException(String.format(
                    "Cannot cast to either Object[][], String[], or List<List<Object>> because data was: %s", data));
        }
        return result;
    }
}
