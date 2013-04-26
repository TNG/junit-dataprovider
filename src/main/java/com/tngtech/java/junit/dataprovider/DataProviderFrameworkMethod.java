package com.tngtech.java.junit.dataprovider;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.runners.model.FrameworkMethod;

/**
 * A special framework method that allows the usage of parameters for the test method.
 */
public class DataProviderFrameworkMethod extends FrameworkMethod {

    /**
     * Index of exploded test method such that each get a unique name.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     */
    final int idx;

    /**
     * Parameters to invoke the test method.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     */
    final Object[] parameters;

    public DataProviderFrameworkMethod(Method method, int idx, Object[] parameters) {
        super(method);
        this.idx = idx;
        if (parameters == null) {
            throw new IllegalArgumentException("parameter must not be null");
        }
        if (parameters.length == 0) {
            throw new IllegalArgumentException("parameter must not be empty");
        }
        this.parameters = Arrays.copyOf(parameters, parameters.length);
    }

    @Override
    public String getName() {
        // don't print last value, if is the expected one
        return String.format("%s [%d: %s]", super.getName(), idx,
                formatParameters(parameters));
    }

    @Override
    public Object invokeExplosively(Object target, Object... params) throws Throwable {
        return super.invokeExplosively(target, parameters);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + idx;
        result = prime * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DataProviderFrameworkMethod other = (DataProviderFrameworkMethod) obj;
        if (idx != other.idx) {
            return false;
        }
        if (!Arrays.equals(parameters, other.parameters)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the given parameters. The parameters are converted to string by the following
     * rules:
     * <ul>
     * <li>null -&gt; &lt;null&gt;</li>
     * <li>empty string -&gt; &lt;empty string&gt;</li>
     * <li>other -&gt; Object.toString</li>
     * </ul>
     *
     * @param parameters the parameters are converted to a comma-separated string
     * @return a string representation of the given parameters
     */
    private String formatParameters(Object[] parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            if (param != null) {
                if (param.getClass().isArray()) {
                    stringBuilder.append("<array>");
                } else if (param instanceof String && ((String) param).isEmpty()) {
                    stringBuilder.append("<empty string>");
                } else {
                    // \\p{C} => invisible control characters and unused code points.
                    stringBuilder.append(param.toString().replaceAll("\\p{C}", "?"));

                    // String toPrint = param.toString();
                    // for (int j = 0; j < toPrint.length(); j++) {
                    // char character = toPrint.charAt(j);
                    //
                    // if (0x00 <= character && character <= 0x1F) { // no non-printable characters
                    // stringBuilder.append('?');
                    // } else {
                    // stringBuilder.append(character);
                    // }
                    // }
                }
            } else {
                stringBuilder.append("<null>");
            }
            if (i < parameters.length - 1) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }

    public static String encodeHTML(String s) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>') {
                out.append("&#" + (int) c + ";");
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
