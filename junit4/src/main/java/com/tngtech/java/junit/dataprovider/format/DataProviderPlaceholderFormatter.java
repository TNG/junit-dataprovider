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
package com.tngtech.java.junit.dataprovider.format;

import java.lang.reflect.Method;
import java.util.List;

import com.tngtech.junit.dataprovider.format.DataProviderTestNameFormatter;
import com.tngtech.junit.dataprovider.placeholder.BasePlaceholder;
import com.tngtech.junit.dataprovider.placeholder.ReplacementData;

public class DataProviderPlaceholderFormatter implements DataProviderTestNameFormatter {

    private final String nameFormat;
    private final List<? extends BasePlaceholder> placeholders;

    public DataProviderPlaceholderFormatter(String nameFormat, List<? extends BasePlaceholder> placeholders) {
        this.nameFormat = nameFormat;
        this.placeholders = placeholders;
    }

    @Override
    public String format(Method testMethod, int invocationIndex, List<Object> arguments) {
        String result = nameFormat;
        for (BasePlaceholder placeholder : placeholders) {
            if (placeholder instanceof com.tngtech.java.junit.dataprovider.internal.placeholder.BasePlaceholder) {
                com.tngtech.java.junit.dataprovider.internal.placeholder.BasePlaceholder placeHolder = (com.tngtech.java.junit.dataprovider.internal.placeholder.BasePlaceholder) placeholder;
                synchronized (placeHolder) {
                    placeHolder.setContext(testMethod, invocationIndex, arguments.toArray());
                    result = placeHolder.process(result);
                }

            } else {
                ReplacementData data = ReplacementData.of(testMethod, invocationIndex, arguments);
                result = placeholder.process(data, result);
            }
        }
        return result;
    }
}
