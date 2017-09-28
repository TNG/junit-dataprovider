package com.tngtech.java.junit.dataprovider;

import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

public class DataProviders {

    /**
     * Helper method to create an {@link Object} array containing all the given arguments, e.g.
     *
     * <pre>
     * <code>
     * Object[] a = $("test", 4);
     * </code>
     * </pre>
     *
     * @param args which should be contained in the resulting {@link Object} array
     * @return an {@link Object} array containing all the given {@code args}
     * @see #$$
     */
    public static Object[] $(Object... args) { // define it with <T> produces var-args warning on user side for java < 6
        return args;
    }

    /**
     * Helper method to create an array of the given {@link Object} arrays, e.g.
     *
     * <pre>
     * <code>
     * // @formatter:off
     * Object[][] b = $$(
     *          $("",        0),
     *          $("test",    4),
     *          $("foo bar", 7),
     *      );
     * // @formatter:on
     * </code>
     * </pre>
     *
     * @param args which should be contained in the resulting array of {@link Object} array
     * @return an array of {@link Object} arrays containing all the given {@code args}
     * @see #$
     */
    public static Object[][] $$(Object[]... args) {
        return args;
    }

    /**
     * Creates a dataprovider test for each argument.
     *
     * @param args which are wrapped in {@link Object} arrays and combined to {@link Object}{@code [][]}
     * @return an array which contains {@link Object} arrays for each single argument
     */
    public static Object[][] testForEach(Object... args) {
        Object[][] result = new Object[args.length][1];
        for (int idx = 0; idx < args.length; idx++) {
            result[idx][0] = args[idx];
        }
        return result;
    }

    /**
     * Creates a dataprovider test for each element in the given {@link Iterable}.
     *
     * @param <T> the type of elements returned by the given {@link Iterable}
     * @param args which are wrapped in {@link Object} arrays and combined to {@link Object}{@code [][]}
     * @return an array which contains {@link Object} arrays for each single element in the given {@link Iterable}
     * @throws NullPointerException iif given {@code args} is {@code null}
     * @deprecated since 1.12.0 {@link Iterable}{@code <?>} can directly be returned from any dataprovider method
     */
    @Deprecated
    public static <T> Object[][] testForEach(Iterable<T> args) {
        checkNotNull(args, "args must not be null");

        List<T> list = new ArrayList<T>();
        for (T arg : args) {
            list.add(arg);
        }
        return testForEach(list.toArray());
    }

    /**
     * Creates a dataprovider test for each value in the given {@link Enum} class.
     *
     * @param <E> the type of the enum type subclass modeled by the given {@code Class}
     * @param enumClass for which each value is wrapped into an array of {@link Object} arrays
     * @return an array which contains {@link Object} arrays for each single value in the given {@link Enum}
     * @throws NullPointerException iif given {@code enumClass} is {@code null}
     */
    public static <E extends Enum<E>> Object[][] testForEach(Class<E> enumClass) {
        checkNotNull(enumClass, "enumClass must not be null");
        return testForEach((Object[]) enumClass.getEnumConstants());
    }

    /**
     * Creates a dataprovider test for each combination of elements of the two provided data providers.
     *
     * <pre>
     * <code>
     * Object[][] r = crossProduct(dataProviderMethod1, dataProviderMethod2);
     * </code>
     * </pre>
     *
     * @param rows1 of first dataprovider which should be cross producted with the second
     * @param rows2 of second dataprovider which should be cross producted with the first
     * @return an {@link Object} array array containing the cross product of the given {@code rows}
     */
    public static Object[][] crossProduct(Object[][] rows1, Object[][] rows2) {
        Object[][] rowsOut = new Object[rows1.length * rows2.length][];
        int indexOut = 0;
        for( int index1 = 0; index1 < rows1.length; index1++ ) {
            Object[] row1 = rows1[index1];
            for( int index2 = 0; index2 < rows2.length; index2++ ) {
                Object[] row2 = rows2[index2];
                Object[] rowOut = new Object[row1.length + row2.length];
                System.arraycopy( row1, 0, rowOut, 0, row1.length );
                System.arraycopy( row2, 0, rowOut, row1.length, row2.length );
                rowsOut[indexOut] = rowOut;
                indexOut++;
            }
        }
        return rowsOut;
    }
}
