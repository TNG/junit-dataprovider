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
package com.tngtech.java.junit.dataprovider;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;

/**
 * Interface to be used to implement a dataprovider method resolver based on the test method. The resolver can be specified for test case
 * using {@link UseDataProvider#resolver()}. The provided resolvers are executed according to the provide strategy in
 * {@link UseDataProvider#resolveStrategy()}.
 *
 */
public interface DataProviderMethodResolver {

    /**
     * Returns the dataprovider methods that belongs to the given test method or an empty {@link List} if no such dataprovider method could
     * be found.
     *
     * @param testMethod test method that uses a dataprovider
     * @param useDataProvider {@link UseDataProvider} annoation on the given test method
     * @return the resolved dataprovider methods or an empty {@link List} if dataprovider could not be found (never {@code null})
     * @throws IllegalArgumentException if given {@code testMethod} is {@code null}
     */
    List<FrameworkMethod> resolve(FrameworkMethod testMethod, UseDataProvider useDataProvider);
}
