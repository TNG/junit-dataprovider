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
package com.tngtech.java.junit.dataprovider.internal.placeholder;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.tngtech.junit.dataprovider.placeholder.ReplacementData;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS", justification = "for backwards compatibility and easier migration to core")
public class SimpleClassNamePlaceholder extends com.tngtech.junit.dataprovider.placeholder.SimpleClassNamePlaceholder {

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
}
