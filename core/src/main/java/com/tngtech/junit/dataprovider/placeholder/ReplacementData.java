package com.tngtech.junit.dataprovider.placeholder;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Immutable class representing the data to format the dataprovider test case name using placeholders.
 */
public class ReplacementData {

    /**
     * Creates {@link ReplacementData} for the given values.
     *
     * @param testMethod to be executed
     * @param testIndex the index (row) of the test / used dataprovider
     * @param parameters used for invoking this test testMethod
     * @return {@link ReplacementData} containing the given values
     * @throws NullPointerException if and only if a given value is {@code null}
     */
    public static ReplacementData of(Method testMethod, int testIndex, List<Object> parameters) {
        return new ReplacementData(testMethod, testIndex, parameters);
    }

    private final Method testMethod;
    private final int testIndex;
    private final List<Object> parameters;

    private ReplacementData(Method testMethod, int testIndex, List<Object> parameters) {
        this.testMethod = checkNotNull(testMethod, "'testMethod' must not be null");
        this.testIndex = testIndex;
        this.parameters = new ArrayList<Object>(checkNotNull(parameters, "'parameters' must not be null"));
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public int getTestIndex() {
        return testIndex;
    }

    public List<Object> getParameters() {
        return unmodifiableList(parameters);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + testIndex;
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + ((testMethod == null) ? 0 : testMethod.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ReplacementData other = (ReplacementData) obj;
        if (testIndex != other.testIndex) {
            return false;
        }
        if (parameters == null) {
            if (other.parameters != null) {
                return false;
            }
        } else if (!parameters.equals(other.parameters)) {
            return false;
        }
        if (testMethod == null) {
            if (other.testMethod != null) {
                return false;
            }
        } else if (!testMethod.equals(other.testMethod)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ReplacementData [testMethod=" + testMethod + ", testIndex=" + testIndex + ", parameters="
                + parameters + "]";
    }
}
