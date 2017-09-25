package com.tngtech.java.junit.dataprovider.internal.placeholder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.Placeholders;

/**
 * Base class for all placeholder which are used to format the test method name. One can create his/her own placeholder
 * extending this and overriding {@link #getReplacementFor(String)}. Afterwards add the new placeholder to the other
 * placeholders by using {@link Placeholders#all()} (for details see {@link Placeholders}).
 *
 * @see DataProvider#format()
 * @see Placeholders
 */
public abstract class BasePlaceholder {

    private final Pattern pattern;

    protected Method method;
    protected int idx;
    protected Object[] parameters;

    /**
     * @param placeholderRegex - regular expression to match the placeholder in the {@link DataProvider#format()}.
     */
    public BasePlaceholder(String placeholderRegex) {
        this.pattern = Pattern.compile(placeholderRegex);
    }

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
        StringBuffer sb = new StringBuffer();

        Matcher matcher = pattern.matcher(formatPattern);
        while (matcher.find()) {
            matcher.appendReplacement(sb, Matcher.quoteReplacement(getReplacementFor(matcher.group())));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Generate and returns the replacement for the found and given placeholder.
     *
     * @param placeholder for which the replacement {@link String} should be returned
     * @return the replacement for the given {@code placeholder} (not {@code null})
     */
    protected abstract String getReplacementFor(String placeholder);
}
