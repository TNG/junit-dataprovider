package com.tngtech.java.junit.dataprovider;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.runners.model.FrameworkMethod;

import com.tngtech.java.junit.dataprovider.internal.TestFormatter;

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

    /**
     * Formatter for this test method.
     */
    private TestFormatter testFormatter;


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
        this.testFormatter = new TestFormatter(); // set default testFormatter
    }

    @Override
    public String getName() {
        return testFormatter.format(getMethod(), idx, parameters);
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
     * Parameters formatter to format parameters of the test method.
     * <p>
     * This method exists and is package private (= visible) only for testing.
     * </p>
     */
    void setFormatter(TestFormatter testFormatter) {
        this.testFormatter = testFormatter;
    }
}
