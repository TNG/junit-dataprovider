package com.tngtech.java.junit.dataprovider.internal.placeholder;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.Placeholders;
import com.tngtech.junit.dataprovider.placeholder.ReplacementData;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Base class for all placeholder which are used to format the test method name. One can create his/her own placeholder
 * extending this and overriding {@link #getReplacementFor(String)}. Afterwards add the new placeholder to the other
 * placeholders by using {@link Placeholders#all()} (for details see {@link Placeholders}).
 * <p>
 * Note: For new and future-proof implementations, please inherit from
 * {@link com.tngtech.junit.dataprovider.placeholder.BasePlaceholder} and use
 * {@link #getReplacementFor(String, ReplacementData)} instead.
 *
 * @see DataProvider#format()
 * @see Placeholders
 *
 * @deprecated Use {@link com.tngtech.junit.dataprovider.placeholder.BasePlaceholder} of {@code core/} instead. JUnit4
 *             internals can handle both. This class will be removed in version 3.0.
 */
@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS", justification = "for backwards compatibility and easier migration to core")
@Deprecated
public abstract class BasePlaceholder extends com.tngtech.junit.dataprovider.placeholder.BasePlaceholder {

    protected Method method;
    protected int idx;
    protected Object[] parameters;

    /**
     * @param placeholderRegex - regular expression to match the placeholder in the {@link DataProvider#format()}.
     */
    public BasePlaceholder(String placeholderRegex) {
        super(placeholderRegex);
    }

    /**
     * Sets the given arguments as context for processing or replacement generation, respectively.
     * <p>
     * Note: For new and future-proof implementations, please inherit from
     * {@link com.tngtech.junit.dataprovider.placeholder.BasePlaceholder} and use
     * {@link #getReplacementFor(String, ReplacementData)} instead of {@link #setContext(Method, int, Object[])} and
     * {@link #getReplacementFor(String)}.
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
     * <p>
     * Note: For new and future-proof implementations, please inherit from
     * {@link com.tngtech.junit.dataprovider.placeholder.BasePlaceholder} and use
     * {@link #process(ReplacementData, String)} instead.
     *
     * @param formatPattern to be processed
     * @return the given {@code formatPattern} containing the generated replacements instead of matching patterns
     */
    public String process(String formatPattern) {
        ReplacementData data = ReplacementData.of(method, idx, Arrays.asList(parameters));
        return super.process(data, formatPattern);
    }

    /**
     * Generate and returns the replacement for the found and given placeholder.
     * <p>
     * Note: For new and future-proof implementations, please inherit from
     * {@link com.tngtech.junit.dataprovider.placeholder.BasePlaceholder} and use
     * {@link #getReplacementFor(String, ReplacementData)} instead.
     *
     * @param placeholder for which the replacement {@link String} should be returned
     * @return the replacement for the given {@code placeholder} (not {@code null})
     */
    protected abstract String getReplacementFor(String placeholder);
}
