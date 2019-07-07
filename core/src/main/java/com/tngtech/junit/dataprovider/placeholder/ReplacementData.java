/*
 * Copyright 2019 TNG Technology Consulting GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
     * @param arguments used for invoking this test testMethod
     * @return {@link ReplacementData} containing the given values
     * @throws NullPointerException if and only if a given value is {@code null}
     */
    public static ReplacementData of(Method testMethod, int testIndex, List<Object> arguments) {
        return new ReplacementData(testMethod, testIndex, arguments);
    }

    private final Method testMethod;
    private final int testIndex;
    private final List<Object> arguments;

    private ReplacementData(Method testMethod, int testIndex, List<Object> arguments) {
        this.testMethod = checkNotNull(testMethod, "'testMethod' must not be null");
        this.testIndex = testIndex;
        this.arguments = new ArrayList<Object>(checkNotNull(arguments, "'arguments' must not be null"));
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public int getTestIndex() {
        return testIndex;
    }

    public List<Object> getArguments() {
        return unmodifiableList(arguments);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + testIndex;
        result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
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
        if (arguments == null) {
            if (other.arguments != null) {
                return false;
            }
        } else if (!arguments.equals(other.arguments)) {
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
        return "ReplacementData [testMethod=" + testMethod + ", testIndex=" + testIndex + ", arguments="
                + arguments + "]";
    }
}
