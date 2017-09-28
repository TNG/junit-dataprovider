package com.tngtech.java.junit.dataprovider.internal.placeholder;

import java.util.Arrays;

import com.tngtech.java.junit.dataprovider.DataProvider;

/**
 * This placeholder format the parameters of a dataprovider test as comma-separated {@link String} according to the
 * given index or range subscript (see {@link DataProvider#format()}. Furthermore the following parameter values are
 * treated specially:
 * <table summary="Special {@link String} treatment">
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

    /**
     * {@link String} representation of {@code null}
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    static final String STRING_NULL = "<null>";

    /**
     * {@link String} representation of {@code ""}
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    static final String STRING_EMPTY = "<empty string>";

    /**
     * {@link String} representation of an non-printable character
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    static final String STRING_NON_PRINTABLE = "<np>";


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
     * Formats the given parameters by retrieving it's {@link String} representation and separate it by comma (=
     * {@code ,}).
     *
     * @param parameters to be formatted
     * @return the {@link String} representation of the given {@link Object}{@code []}
     */
    protected String formatAll(Object[] parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            stringBuilder.append(format(parameters[i]));
            if (i < parameters.length - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    protected String format(Object param) {
        if (param == null) {
            return STRING_NULL;

        } else if (param.getClass().isArray()) {
            if (param.getClass().getComponentType().isPrimitive()) {
                return formatPrimitiveArray(param);
            }
            return "[" + formatAll((Object[]) param) + "]";

        } else if (param instanceof String && ((String) param).isEmpty()) {
            return STRING_EMPTY;

        }

        String result;
        if (param instanceof String) {
            result = (String) param;
        } else {
            result = param.toString();
        }
        if (result == null) {
            return STRING_NULL;
        }
        result = result.replaceAll("\0", "\\\\0").replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
        return replaceNonPrintableChars(result, STRING_NON_PRINTABLE);
    }

    private String formatPrimitiveArray(Object primitiveArray) {
        Class<?> componentType = primitiveArray.getClass().getComponentType();

        if (boolean.class.equals(componentType)) {
            return Arrays.toString((boolean[]) primitiveArray);

        } else if (byte.class.equals(componentType)) {
            return Arrays.toString((byte[]) primitiveArray);

        } else if (char.class.equals(componentType)) {
            return Arrays.toString((char[]) primitiveArray);

        } else if (short.class.equals(componentType)) {
            return Arrays.toString((short[]) primitiveArray);

        } else if (int.class.equals(componentType)) {
            return Arrays.toString((int[]) primitiveArray);

        } else if (long.class.equals(componentType)) {
            return Arrays.toString((long[]) primitiveArray);

        } else if (float.class.equals(componentType)) {
            return Arrays.toString((float[]) primitiveArray);

        } else if (double.class.equals(componentType)) {
            return Arrays.toString((double[]) primitiveArray);
        }
        return "";
    }

    private String replaceNonPrintableChars(String input, String replacement) {
        StringBuilder result = new StringBuilder();
        for (int offset = 0; offset < input.length(); ) {
            int codePoint = input.codePointAt(offset);
            offset += Character.charCount(codePoint);

            // Replace invisible control characters and unused code points
            switch (Character.getType(codePoint)) {
                case Character.CONTROL:     // \p{Cc}
                case Character.FORMAT:      // \p{Cf}
                case Character.PRIVATE_USE: // \p{Co}
                case Character.SURROGATE:   // \p{Cs}
                case Character.UNASSIGNED:  // \p{Cn}
                    result.append(replacement);
                    break;

                default:
                    result.append(Character.toChars(codePoint));
                    break;
            }
        }
        return result.toString();
    }
}
