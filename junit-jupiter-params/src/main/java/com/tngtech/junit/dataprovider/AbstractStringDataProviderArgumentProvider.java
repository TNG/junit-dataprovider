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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.convert.DataConverter;

/**
 * Abstract class which provides the default implementation for creating a custom dataprovider annotation that provides
 * the data directly within itself.
 *
 * @param <SOURCE_ANNOTATION> annotation type used to provide the source data
 *
 * @see DataProvider#value()
 * @see StringDataProviderArgumentProvider
 */
public abstract class AbstractStringDataProviderArgumentProvider<SOURCE_ANNOTATION extends Annotation>
        extends AbstractDataProviderArgumentProvider<SOURCE_ANNOTATION> {

    protected AbstractStringDataProviderArgumentProvider(DataConverter dataConverter) {
        super(dataConverter);
    }

    protected AbstractStringDataProviderArgumentProvider() {
        super();
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        Method testMethod = extensionContext.getRequiredTestMethod();
        return convertData(testMethod, getData(sourceAnnotation), getConverterContext(sourceAnnotation));
    }

    /**
     * @param annotation on the test method which is providing the test data; never {@code null}
     * @return the test data to be converted and used with the annotated test method; never {@code null}
     */
    protected abstract Object getData(SOURCE_ANNOTATION annotation);

    /**
     * @param annotation on the test method which is providing the converter context; never {@code null}
     * @return the converter context used to convert the data to be used with the annotated test method; never
     *         {@code null}
     */
    protected abstract ConverterContext getConverterContext(SOURCE_ANNOTATION annotation);
}
