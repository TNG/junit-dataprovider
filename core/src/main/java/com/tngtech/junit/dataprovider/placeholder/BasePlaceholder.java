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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for all placeholders which are used to format the test method name. One can create his/her own placeholder
 * extending this and overriding {@link #getReplacementFor(String, ReplacementData)}. Afterwards add the new placeholder
 * to the other placeholders.
 */
public abstract class BasePlaceholder {

    private final Pattern pattern;

    /**
     * @param placeholderRegex - regular expression to match the placeholder in the dataprovider format.
     */
    public BasePlaceholder(String placeholderRegex) {
        this.pattern = Pattern.compile(placeholderRegex);
    }

    /**
     * Executes this placeholder for the given {@link String} by searching all occurrences of the regular expression
     * supplied in the constructor and replaces them with the retrieved replacement from
     * {@link #getReplacementFor(String, ReplacementData)}. If the regular expression does not match, an exact copy of
     * the given {@link String} is returned.
     *
     * @param data used to process given {@code formatPattern}
     * @param formatPattern to be processed
     * @return the given {@code formatPattern} containing the generated replacements instead of matching patterns
     */
    public String process(ReplacementData data, String formatPattern) {
        StringBuffer sb = new StringBuffer();

        Matcher matcher = pattern.matcher(formatPattern);
        while (matcher.find()) {
            matcher.appendReplacement(sb, Matcher.quoteReplacement(getReplacementFor(matcher.group(), data)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Generate and returns the replacement for the found and given placeholder.
     *
     * @param placeholder for which the replacement {@link String} should be returned
     * @param data used to generated the replacement for found {@code placeholder}
     * @return the replacement for the given {@code placeholder} (not {@code null})
     */
    protected abstract String getReplacementFor(String placeholder, ReplacementData data);
}
