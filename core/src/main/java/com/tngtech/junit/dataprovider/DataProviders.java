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
package com.tngtech.junit.dataprovider;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
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
     * Creates a dataprovider test for each value in the given {@link Enum} class.
     *
     * @param <E> the type of the enum type subclass modeled by the given {@code Class}
     * @param enumClass for which each value is wrapped into an array of {@link Object} arrays
     * @return an array which contains {@link Object} arrays for each single value in the given {@link Enum}
     * @throws NullPointerException if and only if given {@code enumClass} is {@code null}
     */
    public static <E extends Enum<E>> Object[][] testForEach(Class<E> enumClass) {
        checkNotNull(enumClass, "'enumClass' must not be null");
        return testForEach((Object[]) enumClass.getEnumConstants());
    }

    /**
     * Creates a dataprovider test for each combination of elements of the two provided dataproviders.
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
        for (Object[] row1 : rows1) {
            for (Object[] row2 : rows2) {
                Object[] rowOut = new Object[row1.length + row2.length];
                System.arraycopy(row1, 0, rowOut, 0, row1.length);
                System.arraycopy(row2, 0, rowOut, row1.length, row2.length);
                rowsOut[indexOut++] = rowOut;
            }
        }
        return rowsOut;
    }

    /**
     * Creates a dataprovider test for each combination of elements of the two provided single arg dataproviders.
     *
     * <pre>
     * <code>
     * Object[][] r = crossProduct(dataProviderMethod1, dataProviderMethod2);
     * </code>
     * </pre>
     *
     * @param rows1 of first single arg dataprovider which should be cross producted with the second
     * @param rows2 of second single arg dataprovider which should be cross producted with the first
     * @return an {@link Object} array array containing the cross product of the given {@code rows}
     */
    public static Object[][] crossProductSingleArg(Object[] rows1, Object[] rows2) {
        Object[][] rowsOut = new Object[rows1.length * rows2.length][];
        int indexOut = 0;
        for (Object entry1 : rows1) {
            for (Object entry2 : rows2) {
                rowsOut[indexOut++] = new Object[] { entry1, entry2 };
            }
        }
        return rowsOut;
    }

    /**
     * Creates a dataprovider test for each combination of elements of the two provided {@link Iterable}s of
     * {@link Iterable} dataproviders.
     *
     * <pre>
     * <code>
     * Object[][] r = crossProduct(iterableOfIterable1, iterableOfIterable2);
     * </code>
     * </pre>
     *
     * @param rows1 of first {@link Iterable} of {@link Iterable} which should be cross producted with the second
     * @param rows2 of second {@link Iterable} of {@link Iterable} which should be cross producted with the first
     * @param <T> inner type of first {@link Iterable} parameter in order to also support covariance for inner types,
     *            e.g. {@code List<List<Integer>>}
     * @param <V> inner type of second {@link Iterable} parameter in order to also support covariance for inner types,
     *            e.g. {@code List<List<Integer>>}
     * @return an {@link Object} array array containing the cross product of the given {@code rows}
     */
    public static <T extends Iterable<?>, V extends Iterable<?>> Object[][] crossProduct(Iterable<T> rows1,
            Iterable<V> rows2) {
        List<List<Object>> rowsOut = new ArrayList<List<Object>>();
        for (Iterable<?> row1 : rows1) {
            for (Iterable<?> row2 : rows2) {
                rowsOut.add(concat(row1, row2));
            }
        }
        return convert(rowsOut);
    }

    /**
     * Creates a dataprovider test for each combination of elements of the two provided single arg {@link Iterable}s
     * dataprovider.
     *
     * <pre>
     * <code>
     * Object[][] r = crossProduct(iterable1, iterable2);
     * </code>
     * </pre>
     *
     * @param rows1 of first single arg {@link Iterable} dataprovider which should be cross producted with the second
     * @param rows2 of second single arg {@link Iterable} dataprovider which should be cross producted with the first
     * @return an {@link Object} array array containing the cross product of the given {@code rows}
     */
    public static Object[][] crossProductSingleArg(Iterable<?> rows1, Iterable<?> rows2) {
        List<List<Object>> rowsOut = new ArrayList<List<Object>>();
        for (Object row1 : rows1) {
            for (Object row2 : rows2) {
                rowsOut.add(Arrays.asList(row1, row2));
            }
        }
        return convert(rowsOut);
    }

    private static List<Object> concat(Iterable<?> row1, Iterable<?> row2) {
        List<Object> outs = new ArrayList<Object>();
        for (Object t : row1) {
            outs.add(t);
        }
        for (Object v : row2) {
            outs.add(v);
        }
        return outs;
    }

    private static Object[][] convert(List<List<Object>> rows) {
        Object[][] result = new Object[rows.size()][];
        int indexOut = 0;
        for (List<Object> row : rows) {
            result[indexOut++] = row.toArray();
        }
        return result;
    }
}
