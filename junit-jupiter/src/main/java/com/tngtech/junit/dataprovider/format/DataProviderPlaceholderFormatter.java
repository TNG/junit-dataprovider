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
package com.tngtech.junit.dataprovider.format;

import java.lang.reflect.Method;
import java.util.List;

import com.tngtech.junit.dataprovider.placeholder.BasePlaceholder;
import com.tngtech.junit.dataprovider.placeholder.ReplacementData;

public class DataProviderPlaceholderFormatter implements DataProviderTestNameFormatter {

    private final String format;
    private final List<? extends BasePlaceholder> placeholders;

    public DataProviderPlaceholderFormatter(String format, List<? extends BasePlaceholder> placeholders) {
        this.format = format;
        this.placeholders = placeholders;
    }

    @Override
    public String format(Method testMethod, int invocationIndex, List<Object> arguments) {
        ReplacementData data = ReplacementData.of(testMethod, invocationIndex, arguments);

        String result = format;
        for (BasePlaceholder placeHolder : placeholders) {
            result = placeHolder.process(data, result);
        }
        return result;
    }
}
