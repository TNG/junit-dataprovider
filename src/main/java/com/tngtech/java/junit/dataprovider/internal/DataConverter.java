package com.tngtech.java.junit.dataprovider.internal;

import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkArgument;
import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.internal.convert.ObjectArrayConverter;
import com.tngtech.java.junit.dataprovider.internal.convert.SingleArgConverter;
import com.tngtech.java.junit.dataprovider.internal.convert.StringConverter;

/**
 * Internal class to convert some data to its corresponding parameters.
 */
public class DataConverter {

    private ObjectArrayConverter objectArrayConverter;
    private SingleArgConverter singleArgConverter;
    private StringConverter stringConverter;

    public DataConverter() {
        this.objectArrayConverter = new ObjectArrayConverter();
        this.singleArgConverter = new SingleArgConverter();
        this.stringConverter = new StringConverter();
    }

    /**
     * Returns {@code true} iif this {@link DataConverter} can convert the given {@code type}. Currently supported {@code type}s:
     * <ul>
     * <li>Object[][]</li>
     * <li>Iterable&lt;Iterable&lt;?&gt;&gt;</li>
     * <li>Iterable&lt;?&gt;</li>
     * <li>Object[]</li>
     * <li>String[]</li>
     * </ul>
     *
     * Please note, that {@link Iterable} can be replaced by any valid subtype (checked via {@link Class#isAssignableFrom(Class)}). As well
     * as an arbitrary inner type is also accepted. Only rawtypes are not supported currently.
     *
     * @param type to be checked for convertibility (use either {@link Method#getGenericReturnType()}, {@link Method#getReturnType()}, or
     *            simple {@link Class} if possible)
     * @return {@code true} iif given {@code type} can be converted.
     */
    public boolean canConvert(Type type) {
        if (type instanceof Class) {
            return Object[][].class.equals(type) || Object[].class.equals(type) || String[].class.equals(type);
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            Type rawType = parameterizedType.getRawType();
            if (Iterable.class.isAssignableFrom((Class<?>) rawType)) {
                return canConvertIterableOf(parameterizedType);
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
        checkNotNull(parameterTypes, "parameterTypes must not be null");
        checkNotNull(dataProvider, "dataProvider must not be null");
        checkArgument(parameterTypes.length != 0, "parameterTypes must not be empty");

        if (data instanceof Object[][]) {
            return convert((Object[][]) data, isVarArgs, parameterTypes);

        } else if (data instanceof String[]) {
            return convert((String[]) data, isVarArgs, parameterTypes, dataProvider);

        } else if (data instanceof Object[]) {
            return convert((Object[]) data, isVarArgs, parameterTypes);

        } else if (data instanceof Iterable) {
            @SuppressWarnings("rawtypes")
            Iterable iterableData = (Iterable) data;
            return convert(iterableData, isVarArgs, parameterTypes);

        }
        throw new ClassCastException(
                String.format("Cannot cast to either Object[][], Object[], String[], or Iterable because data was: %s", data));
    }

    private boolean canConvertIterableOf(ParameterizedType parameterizedType) {
        if (parameterizedType.getActualTypeArguments().length == 1) {
            Type innerType = parameterizedType.getActualTypeArguments()[0];
            if (parameterizedType.getActualTypeArguments()[0] instanceof ParameterizedType) {
                ParameterizedType innerType2 = (ParameterizedType) innerType;
                return Iterable.class.isAssignableFrom((Class<?>) innerType2.getRawType());
            }
            return true;
        }
        return false;
    }

    private List<Object[]> convert(Object[][] data, boolean isVarArgs, Class<?>[] parameterTypes) {
        List<Object[]> result = new ArrayList<Object[]>();
        for (Object[] arguments : data) {
            result.add(objectArrayConverter.convert(arguments, isVarArgs, parameterTypes));
        }
        return result;
    }

    private List<Object[]> convert(String[] data, boolean isVarArgs, Class<?>[] parameterTypes, DataProvider dataProvider) {
        List<Object[]> result = new ArrayList<Object[]>();
        int idx = 0;
        for (String argString : data) {
            result.add(stringConverter.convert(argString, isVarArgs, parameterTypes, dataProvider, idx++));
        }
        return result;
    }

    private List<Object[]> convert(Object[] data, boolean isVarArgs, Class<?>[] parameterTypes) {
        List<Object[]> result = new ArrayList<Object[]>();
        for (Object argument : data) {
            result.add(singleArgConverter.convert(argument, isVarArgs, parameterTypes));
        }
        return result;
    }

    private List<Object[]> convert(Iterable<?> data, boolean isVarArgs, Class<?>[] parameterTypes) {
        List<Object[]> result = new ArrayList<Object[]>();
        for (Object arguments : data) {
            if (arguments != null && Iterable.class.isAssignableFrom(arguments.getClass())) {
                @SuppressWarnings("rawtypes")
                Iterable iterable = (Iterable) arguments;
                result.add(objectArrayConverter.convert(toArray(iterable), isVarArgs, parameterTypes));

            } else {
                result.add(singleArgConverter.convert(arguments, isVarArgs, parameterTypes));
            }
        }
        return result;
    }

    private Object[] toArray(Iterable<?> iterable) {
        List<Object> list = new ArrayList<Object>();
        for (Object element : iterable) {
            list.add(element);
        }
        return list.toArray();
    }

    public void setObjectArrayConverter(ObjectArrayConverter objectArrayConverter) {
        this.objectArrayConverter = objectArrayConverter;
    }

    public void setSingleArgConverter(SingleArgConverter singleArgConverter) {
        this.singleArgConverter = singleArgConverter;
    }

    public void setStringConverter(StringConverter stringConverter) {
        this.stringConverter = stringConverter;
    }
}
