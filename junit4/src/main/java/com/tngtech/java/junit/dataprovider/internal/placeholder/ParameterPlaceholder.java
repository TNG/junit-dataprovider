package com.tngtech.java.junit.dataprovider.internal.placeholder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.placeholder.ArgumentPlaceholder;
import com.tngtech.junit.dataprovider.placeholder.ReplacementData;

/**
 * This placeholder formats the parameters of a dataprovider test as comma-separated {@link String} according to the
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
 *
 * @deprecated Use {@link com.tngtech.junit.dataprovider.placeholder.ArgumentPlaceholder} instead. JUnit4 internals can
 *             handle both. This class will be removed in version 3.0.
 */
@Deprecated
public class ParameterPlaceholder extends ArgumentPlaceholder {

    /**
     * {@link String} representation of {@code null}
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    static final String STRING_NULL = ArgumentPlaceholder.STRING_NULL;

    /**
     * {@link String} representation of {@code ""}
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    static final String STRING_EMPTY = ArgumentPlaceholder.STRING_EMPTY;

    /**
     * {@link String} representation of an non-printable character
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    static final String STRING_NON_PRINTABLE = ArgumentPlaceholder.STRING_NON_PRINTABLE;

    // -- Begin: copied from origin BasePlaceholder for backwards compatibility reasons --------------------------------

    protected Method method;
    protected int idx;
    protected Object[] parameters;

    /**
     * Sets the given arguments as context for processing or replacement generation, respectively.
     *
     * @param method - test method
     * @param idx - index of the dataprovider row
     * @param parameters of the current dataprovider test to be executed
     */
    public void setContext(Method method, int idx, Object[] parameters) {
        this.method = method;
        this.idx = idx;
        this.parameters = Arrays.copyOf(parameters, parameters.length);
    }

    /**
     * Executes this placeholder for the given {@link String} by searching all occurrences of the regular expression
     * supplied in the constructor and replaces them with the retrieved replacement from
     * {@link #getReplacementFor(String)}. If the regular expression does not match, an exact copy of the given
     * {@link String} is returned.
     *
     * @param formatPattern to be processed
     * @return the given {@code formatPattern} containing the generated replacements instead of matching patterns
     */
    public String process(String formatPattern) {
        ReplacementData data = ReplacementData.of(method, idx, Arrays.asList(parameters));
        return super.process(data, formatPattern);
    }

    // -- End: copied from origin BasePlaceholder for backwards compatibility reasons ----------------------------------

    protected String getReplacementFor(String placeholder) {
        ReplacementData data = ReplacementData.of(method, idx, Arrays.asList(parameters));
        return super.getReplacementFor(placeholder, data);
    }

    /**
     * Formats the given parameters by retrieving it's {@link String} representation and separate it by comma (=
     * {@code ,}).
     * <p>
     * Note: For new and future-proof implementations, please use {@link #formatAll(java.util.List)} instead.
     *
     * @param parameters to be formatted
     * @return the {@link String} representation of the given {@link Object}{@code []}
     *
     * @see #formatAll(java.util.List)
     */
    protected String formatAll(Object[] parameters) {
        return super.formatAll(Arrays.asList(parameters));
    }

    @Override
    protected String formatAll(List<Object> arguments) {
        return formatAll(arguments.toArray());
    }

    @Override
    protected String format(Object param) {
        return super.format(param);
    }
}
