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
package com.tngtech.junit.dataprovider.resolver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Interface to be used to implement a dataprovider method resolver based on the given {@link DataProviderResolverContext}.
 */
public interface DataProviderMethodResolver {

    /**
     * Returns the dataprovider methods that belongs to the given test method using the given resolver context or an
     * empty {@link List} if no dataprovider method could be found.
     *
     * @param context for resolving of dataprovider methods
     * @return the resolved dataprovider methods or an empty {@link List} if no dataprovider methods could be found
     * @throws NullPointerException if given {@code context} is {@code null}
     */
    List<Method> resolve(DataProviderResolverContext context);
}
