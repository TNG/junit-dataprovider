package com.tngtech.java.junit.dataprovider;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.runners.model.FrameworkMethod;

/**
 * A special framework method that allows the usage of parameters for the test method.
 */
class DataProviderFrameworkMethod extends FrameworkMethod {

    /** parameters to invoke the test method */
    protected final Object[] parameters;

    /** flag if the last parameter is the expected value and should not be included in the name */
    protected boolean hasExpectedParameter;

    public DataProviderFrameworkMethod(Method method, Object[] parameters, boolean hasExpectedParameter) {
        super(method);
        this.parameters = Arrays.copyOf(parameters, parameters.length);
        this.hasExpectedParameter = hasExpectedParameter;
    }

    @Override
    public String getName() {
        // don't print last value, it is the expected one
        return super.getName()
                + ": "
                + formatParameters(Arrays.copyOf(parameters, hasExpectedParameter ? parameters.length - 1
                        : parameters.length));
    }

    @Override
    public Object invokeExplosively(Object target, Object... params) throws Throwable {
        return super.invokeExplosively(target, parameters);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (hasExpectedParameter ? 1231 : 1237);
        result = prime * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!DataProviderFrameworkMethod.class.isInstance(obj) || !super.equals(obj)) {
            return false;
        }

        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataProviderFrameworkMethod other = (DataProviderFrameworkMethod) obj;
        if (hasExpectedParameter != other.hasExpectedParameter)
            return false;
        if (!Arrays.equals(parameters, other.parameters))
            return false;
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
    protected String formatParameters(Object[] parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] != null) {
                if (parameters[i] instanceof String && parameters[i].toString().isEmpty()) {
                    stringBuilder.append("<empty string>");
                } else {
                    stringBuilder.append(parameters[i]);
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
}
