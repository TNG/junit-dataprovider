package com.tngtech.junit.dataprovider.convert;

import static com.tngtech.junit.dataprovider.Preconditions.checkArgument;
import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;
import static com.tngtech.junit.dataprovider.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to convert dataprovider data (= arguments) to its corresponding parameters.
 */
public class DataConverter {

    /**
     * Converts the given {@link Object} to a {@link List} of {@link Object}{@code []} with {@link Class}es correspond
     * to given {@code parameterTypes}.
     * <p>
     * Supported types are :
     * <ul>
     * <li>Object[][]</li>
     * <li>Iterable&lt;Iterable&lt;?&gt;&gt;</li>
     * <li>Iterable&lt;?&gt;</li>
     * <li>Object[]</li>
     * <li>String[]</li>
     * </ul>
     *
     * Note: {@link Iterable} can be replaced by any valid subtype. As well as an arbitrary inner type is also accepted.
     *
     * @param data to be converted
     * @param isVarargs determines whether test method has a varargs parameter
     * @param parameterTypes required types for {@code data}
     * @param context containing settings which should be used to convert given {@code data} (only required for
     *            converting {@code String[]})
     * @return converted data as {@link List}{@code <}{@link Object}{@code []>} with the required {@code parameterTypes}
     * @throws NullPointerException if and only if given {@code parameterTypes} are {@code null}
     * @throws IllegalArgumentException if and only if given {@code parameterTypes} is empty
     * @throws IllegalStateException if and only if given {@code config} is {@code null} but data is {@code String[]}
     * @throws ClassCastException if and only if {@code data} is not a compatible type
     */
    public List<Object[]> convert(Object data, boolean isVarargs, Class<?>[] parameterTypes, ConverterContext context) {
        checkNotNull(parameterTypes, "'parameterTypes' must not be null");
        checkArgument(parameterTypes.length != 0, "'parameterTypes' must not be empty");

        if (data instanceof Object[][]) {
            return convert((Object[][]) data, isVarargs, parameterTypes, context);

        } else if (data instanceof String[]) {
            checkState(context != null, "'context' must not be null for 'String[]' data");
            return convert((String[]) data, isVarargs, parameterTypes, context);

        } else if (data instanceof Object[]) {
            return convert((Object[]) data, isVarargs, parameterTypes, context);

        } else if (data instanceof Iterable) {
            @SuppressWarnings("rawtypes")
            Iterable iterableData = (Iterable) data;
            return convert(iterableData, isVarargs, parameterTypes, context);
        }
        throw new ClassCastException(
                String.format("Cannot cast to either Object[][], Object[], String[], or Iterable because data was: %s", data));
    }

    private List<Object[]> convert(Object[][] data, boolean isVarargs, Class<?>[] parameterTypes,
            ConverterContext config) {
        List<Object[]> result = new ArrayList<Object[]>();
        for (Object[] arguments : data) {
            result.add(config.getObjectArrayConverter().convert(arguments, isVarargs, parameterTypes));
        }
        return result;
    }

    private List<Object[]> convert(String[] data, boolean isVarargs, Class<?>[] parameterTypes,
            ConverterContext context) {
        List<Object[]> result = new ArrayList<Object[]>();
        int idx = 0;
        for (String argString : data) {
            result.add(context.getStringConverter().convert(argString, isVarargs, parameterTypes, context, idx++));
        }
        return result;
    }

    private List<Object[]> convert(Object[] data, boolean isVarargs, Class<?>[] parameterTypes,
            ConverterContext context) {
        List<Object[]> result = new ArrayList<Object[]>();
        for (Object argument : data) {
            result.add(context.getSingleArgConverter().convert(argument, isVarargs, parameterTypes));
        }
        return result;
    }

    private List<Object[]> convert(Iterable<?> data, boolean isVarargs, Class<?>[] parameterTypes,
            ConverterContext context) {
        List<Object[]> result = new ArrayList<Object[]>();
        for (Object arguments : data) {
            if (arguments != null && Iterable.class.isAssignableFrom(arguments.getClass())) {
                @SuppressWarnings("rawtypes")
                Iterable iterable = (Iterable) arguments;
                result.add(context.getObjectArrayConverter().convert(toArray(iterable), isVarargs, parameterTypes));

            } else {
                result.add(context.getSingleArgConverter().convert(arguments, isVarargs, parameterTypes));
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
}
