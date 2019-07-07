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

import static com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext.generateLocations;
import static java.util.Arrays.asList;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.ReflectionSupport;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext;

/**
 * Default implementation for using a dataprovider with an "external" dataprovider providing the test data.
 */
class UseDataProviderArgumentProvider
        extends AbstractUseDataProviderArgumentProvider<UseDataProvider, DataProvider> {

    UseDataProviderArgumentProvider() {
        super(DataProvider.class);
    }

    @Override
    public void accept(UseDataProvider sourceAnnotation) {
        this.sourceAnnotation = sourceAnnotation;
    }

    @Override
    protected DataProviderResolverContext getDataProviderResolverContext(ExtensionContext extensionContext,
            UseDataProvider testAnnotation) {
        return new DataProviderResolverContext(extensionContext.getRequiredTestMethod(),
                asList(sourceAnnotation.resolver()), sourceAnnotation.resolveStrategy(),
                generateLocations(extensionContext.getRequiredTestClass(), sourceAnnotation.location()),
                DataProvider.class, sourceAnnotation.value());
    }

    @Override
    protected ConverterContext getConverterContext(DataProvider dataProvider) {
        return new ConverterContext(ReflectionSupport.newInstance(dataProvider.objectArrayConverter()),
                ReflectionSupport.newInstance(dataProvider.singleArgConverter()),
                ReflectionSupport.newInstance(dataProvider.stringConverter()), dataProvider.splitBy(),
                dataProvider.convertNulls(), dataProvider.trimValues(), dataProvider.ignoreEnumCase());
    }

    @Override
    protected boolean cacheDataProviderResult(DataProvider dataProviderAnnotation) {
        return dataProviderAnnotation.cache();
    }
}
