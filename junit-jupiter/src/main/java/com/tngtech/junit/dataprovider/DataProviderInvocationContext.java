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
package com.tngtech.junit.dataprovider;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;
import static java.util.Collections.singletonList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.platform.commons.util.ReflectionUtils;

import com.tngtech.junit.dataprovider.format.DataProviderPlaceholderFormatter;
import com.tngtech.junit.dataprovider.format.DataProviderTestNameFormatter;

class DataProviderInvocationContext implements TestTemplateInvocationContext {

    private final Method testMethod;
    private final List<Object> arguments;
    private final DisplayNameContext displayNameContext;

    DataProviderInvocationContext(Method testMethod, List<Object> arguments, DisplayNameContext displayNameContext) {
        this.testMethod = checkNotNull(testMethod, "'testMethod' must not be null");
        this.arguments = new ArrayList<>(checkNotNull(arguments, "'arguments' must not be null"));
        this.displayNameContext = checkNotNull(displayNameContext, "'displayNameContext' must not be null");
    }

    @Override
    public String getDisplayName(int invocationIndex) {
        Class<? extends DataProviderTestNameFormatter> formatter = displayNameContext.getFormatter();
        if (formatter == null || DataProviderPlaceholderFormatter.class.equals(formatter)) {
            return new DataProviderPlaceholderFormatter(displayNameContext.getFormat(),
                    displayNameContext.getPlaceholders()).format(testMethod, invocationIndex, arguments);
        }
        return ReflectionUtils.newInstance(formatter).format(testMethod, invocationIndex, arguments);
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
        return singletonList(new DataProviderParameterResolver(arguments));
    }
}
