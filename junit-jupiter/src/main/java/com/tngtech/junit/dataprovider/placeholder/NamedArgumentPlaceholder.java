package com.tngtech.junit.dataprovider.placeholder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This placeholder format the arguments including their parameter names of a dataprovider test as comma-separated
 * {@link String} according to the given index or range subscript. Furthermore the following arguments are treated
 * specially:
 * <table summary="Special {@link String} treatment">
 * <tr>
 * <th>Argument value</th>
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
 * Note: Test code must be compiled with "-parameters" option in order to retrieve formal parameter names!
 */
// TODO a lot of equal code to ArgumentPlaceholder, e.g. javadoc above, the constants below ...
public class NamedArgumentPlaceholder extends BasePlaceholder {

    private static final Logger logger = LoggerFactory.getLogger(NamedArgumentPlaceholder.class);

    /**
     * W {@link String} representation of {@code null}
     */
    protected static final String STRING_NULL = "<null>";

    /**
     * {@link String} representation of {@code ""}
     */
    protected static final String STRING_EMPTY = "<empty string>";

    /**
     * {@link String} representation of an non-printable character
     */
    protected static final String STRING_NON_PRINTABLE = "<np>";


    public NamedArgumentPlaceholder() {
        super("%na\\[(-?[0-9]+|-?[0-9]+\\.\\.-?[0-9]+)\\]");
    }

    @Override
    protected String getReplacementFor(String placeholder, ReplacementData data) {
        String subscript = placeholder.substring(4, placeholder.length() - 1);

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

        List<Object> namedArguments = data.getArguments();
        from = (from >= 0) ? from : namedArguments.size() + from;
        to = (to >= 0) ? to + 1 : namedArguments.size() + to + 1;
        return formatAll(getSubArrayOfMethodParameters(data.getTestMethod(), from, to), namedArguments.subList(from, to));
    }

    /**
     * Formats the given parameters and arguments to a comma-separated list of {@code $parameterName=$argumentName}.
     * Arguments {@link String} representation are therefore treated specially.
     *
     * @param parameters used to for formatting
     * @param arguments to be formatted
     * @return the formatted {@link String} of the given {@link Parameter}{@code []} and {@link List}{@code <Object>}
     */
    protected String formatAll(Parameter[] parameters, List<Object> arguments) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int idx = 0; idx < arguments.size(); idx++) {
            String parameterName = (parameters.length > idx) ? parameters[idx].getName() : "?";
            Object argument = arguments.get(idx);
            stringBuilder.append(parameterName).append("=").append(format(argument));
            if (idx < arguments.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", justification = "false positive if 'param.toString()' returns 'null'")
    protected String format(Object param) {
        if (param == null) {
            return STRING_NULL;

        } else if (param.getClass().isArray()) {
            if (param.getClass().getComponentType().isPrimitive()) {
                return formatPrimitiveArray(param);
            }
            return "[" + formatArray((Object[]) param) + "]";

        } else if (param instanceof String && ((String) param).isEmpty()) {
            return STRING_EMPTY;

        }

        String result;
        if (param instanceof String) {
            result = (String) param;
        } else {
            result = param.toString();
        }
        if (result == null) { // maybe null if "param.toString()" returns null
            return STRING_NULL;
        }
        result = result.replaceAll("\0", "\\\\0").replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
        return replaceNonPrintableChars(result, STRING_NON_PRINTABLE);
    }

    private Parameter[] getSubArrayOfMethodParameters(Method testMethod, int fromIndex, int toIndex) {
        Parameter[] parameters = testMethod.getParameters();
        if (parameters.length > 0 && !parameters[0].isNamePresent()) {
            logger.warn(() -> String.format("Parameter names on method '%s' are not available"
                    + ". To store formal parameter names, compile the source file with the '-parameters' option"
                    + ". See also https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html",
                    testMethod));
        }
        return Arrays.copyOfRange(parameters, fromIndex, toIndex);
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

    /**
     * Formats the given arguments by retrieving it's {@link String} representation and separate it by comma (=
     * {@code ,}).
     *
     * @param arguments to be formatted
     * @return the {@link String} representation of the given {@link Object}{@code []}
     */
    private String formatArray(Object[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            stringBuilder.append(format(array[i]));
            if (i < array.length - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
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
