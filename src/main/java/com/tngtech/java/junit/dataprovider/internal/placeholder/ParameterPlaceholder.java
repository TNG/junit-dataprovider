package com.tngtech.java.junit.dataprovider.internal.placeholder;

import java.util.Arrays;

import com.tngtech.java.junit.dataprovider.DataProvider;

/**
 * This placeholder format the parameters of a data provider test as comma-separated {@link String} according to the
 * given index or range subscript (see {@link DataProvider#format()}. Furthermore the following parameter values are
 * treated specially:
 * <table>
 * <tr>
 * <th>Parameter value</th>
 * <th>target {@link String}</th>
 * </tr>
 * <tr>
 * <td>null</td>
 * <td>&lt;null&gt;</td>
 * </tr>
 * <tr>
 * <td>&quot;&quot; (= empty string)</td>
 * <td>&lt;empty string&gt;</td>
 * </tr>
 * <tr>
 * <td>array (e.g. String[])</td>
 * <td>{@code "[" + formatPattern(array) + "]"}</td>
 * </tr>
 * <tr>
 * <td>other</td>
 * <td>{@link Object#toString()}</td>
 * </tr>
 * </table>
 */
public class ParameterPlaceholder extends BasePlaceholder {
    public ParameterPlaceholder() {
        super("%p\\[(-?[0-9]+|-?[0-9]+\\.\\.-?[0-9]+)\\]");
    }

    @Override
    protected String getReplacementFor(String placeholder) {
        String subscript = placeholder.substring(3, placeholder.length() - 1);

        int from = Integer.MAX_VALUE;
        int to = Integer.MIN_VALUE;
        if (subscript.contains("..")) {
            String[] split = subscript.split("\\.\\.");

            from = Integer.parseInt(split[0]);
            to = Integer.parseInt(split[1]);
        } else {
            from = Integer.parseInt(subscript);
            to = from;
        }
        from = (from >= 0) ? from : parameters.length + from;
        to = (to >= 0) ? to + 1 : parameters.length + to + 1;

        return formatAll(Arrays.copyOfRange(parameters, from, to));
    }

    /**
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     */
    String formatAll(Object[] parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            if (param == null) {
                stringBuilder.append("<null>");

            } else if (param.getClass().isArray()) {
                if (param.getClass().getComponentType().isPrimitive()) {
                    appendTo(stringBuilder, param);
                } else {
                    stringBuilder.append('[').append(formatAll((Object[]) param)).append(']');
                }

            } else if (param instanceof String && ((String) param).isEmpty()) {
                stringBuilder.append("<empty string>");

            } else {
                stringBuilder.append(param.toString());
            }

            if (i < parameters.length - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    private void appendTo(StringBuilder stringBuilder, Object primitiveArray) {
        Class<?> componentType = primitiveArray.getClass().getComponentType();

        if (boolean.class.equals(componentType)) {
            stringBuilder.append(Arrays.toString((boolean[]) primitiveArray));

        } else if (byte.class.equals(componentType)) {
            stringBuilder.append(Arrays.toString((byte[]) primitiveArray));

        } else if (char.class.equals(componentType)) {
            stringBuilder.append(Arrays.toString((char[]) primitiveArray));

        } else if (short.class.equals(componentType)) {
            stringBuilder.append(Arrays.toString((short[]) primitiveArray));

        } else if (int.class.equals(componentType)) {
            stringBuilder.append(Arrays.toString((int[]) primitiveArray));

        } else if (long.class.equals(componentType)) {
            stringBuilder.append(Arrays.toString((long[]) primitiveArray));

        } else if (float.class.equals(componentType)) {
            stringBuilder.append(Arrays.toString((float[]) primitiveArray));

        } else if (double.class.equals(componentType)) {
            stringBuilder.append(Arrays.toString((double[]) primitiveArray));
        }
    }
}
