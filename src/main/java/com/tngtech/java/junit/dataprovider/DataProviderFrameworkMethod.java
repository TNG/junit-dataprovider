package com.tngtech.java.junit.dataprovider;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

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

        if (parameters == null) {
            throw new NullPointerException("parameter must not be null");
        }
        if (parameters.length == 0) {
            throw new IllegalArgumentException("parameter must not be empty");
        }

        this.idx = idx;
        this.parameters = Arrays.copyOf(parameters, parameters.length);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("NP_NULL_PARAM_DEREF")
    public DataProviderFrameworkMethod(Method method, int idx, List<Object> parameters) {
        this(method, idx, (parameters == null) ? null : parameters.toArray());
    }

    @Override
    public String getName() {
        return String.format("%s[%d: %s]", super.getName(), idx, format(parameters));
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
     * <li>&quot;%quot; (= empty string) -&gt; &lt;empty string&gt;</li>
     * <li>array (e.g. String[]) -&gt; &lt;array&gt;</li>
     * <li>other -&gt; Object.toString</li>
     * </ul>
     *
     * @param parameters the parameters are converted to a comma-separated string
     * @return a string representation of the given parameters
     */
    private <T> String format(T[] parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            if (param == null) {
                stringBuilder.append("<null>");

            } else if (param.getClass().isArray()) {
                if (param.getClass().getComponentType().isPrimitive()) {
                    appendTo(stringBuilder, param);
                } else {
                    stringBuilder.append('[').append(format(getArray(param))).append(']');
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

    private <T> T[] getArray(Object array) {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) array;
        return result;
    }
}
