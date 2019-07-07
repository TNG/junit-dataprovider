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

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DataProviderMethodResolverHelper {

    /**
     * Method searches for dataprovider methods for the resolver context.
     * <p>
     * If {@link ResolveStrategy} is pointing to {@link ResolveStrategy#UNTIL_FIRST_MATCH}, the first found dataprovider
     * methods of the first resolver will be returned. {@link ResolveStrategy#AGGREGATE_ALL_MATCHES} will aggregate the
     * results of all resolvers such that all found dataprovider methods are returned.
     *
     * @param context to be used for resolving dataprovider methods
     * @return the found dataprovider methods or an empty list
     * @throws NullPointerException if and only if given context is {@code null}
     */
    public static List<Method> findDataProviderMethods(DataProviderResolverContext context) {
        checkNotNull(context, "'context' must not be null");

        List<Method> result = new ArrayList<Method>();
        for (Class<? extends DataProviderMethodResolver> resolverClass : context.getResolverClasses()) {
            DataProviderMethodResolver resolver = newInstance(resolverClass);

            List<Method> dataProviderMethods = resolver.resolve(context);
            if (context.getResolveStrategy() == ResolveStrategy.UNTIL_FIRST_MATCH && !dataProviderMethods.isEmpty()) {
                result.addAll(dataProviderMethods);
                break;

            } else if (context.getResolveStrategy() == ResolveStrategy.AGGREGATE_ALL_MATCHES) {
                result.addAll(dataProviderMethods);
            }
        }
        return result;
    }

    private static <T> T newInstance(Class<T> clazz) {
        Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    String.format("Could not find default constructor to instantiate '%s'.", clazz), e);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(String.format(
                    "Security violation while trying to access default constructor to instantiate '%s'.", clazz), e);
        }
        try {
            return constructor.newInstance();
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                    String.format("Could not access default constructor to instantiate '%s'.", clazz), e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    String.format("Could not instantiate '%s' using default constructor.", clazz), e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(
                    String.format("The default constructor of '%s' has thrown an exception: %s", clazz, e.getMessage()),
                    e);
        }
    }
}