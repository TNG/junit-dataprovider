package com.tngtech.java.junit.dataprovider.internal;

import java.util.Arrays;

public class ParametersFormatter {

    /**
     * Returns a {@link String} representation of the given {@code parameters}. The conversion rules are as follows:
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
     * <td>{@code "[" + format(array) + "]"}</td>
     * </tr>
     * <tr>
     * <td>other</td>
     * <td>{@link Object#toString()}</td>
     * </tr>
     * </table>
     *
     * @param parameters the parameters are converted to a regex-separated {@link String}
     * @return a {@link String} representation of the given parameters
     */
    public String format(Object[] parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            if (param == null) {
                stringBuilder.append("<null>");

            } else if (param.getClass().isArray()) {
                if (param.getClass().getComponentType().isPrimitive()) {
                    appendTo(stringBuilder, param);
                } else {
                    stringBuilder.append('[').append(format((Object[]) param)).append(']');
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
