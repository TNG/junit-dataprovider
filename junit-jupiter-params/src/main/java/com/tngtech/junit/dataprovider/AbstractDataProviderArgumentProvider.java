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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.convert.DataConverter;

/**
 * Abstract, internal argument provider to create {@link Stream} of {@link Arguments}s for dataprovider tests.
 *
 * @param <SOURCE_ANNOTATION> annotation type used on test method for which this argument provider is concerned
 */
abstract class AbstractDataProviderArgumentProvider<SOURCE_ANNOTATION extends Annotation>
        implements ArgumentsProvider, AnnotationConsumer<SOURCE_ANNOTATION> {

    /**
     * The {@link DataConverter} to be used to convert from supported return types of any dataprovider to {@link List}
     * {@code <}{@link Object}{@code []>} such that data can be further handled. Defaults to {@link DataConverter}.
     */
    private final DataConverter dataConverter;

    /**
     * The annotation used on the test method where dataprovider tests should be generated for.
     */
    protected SOURCE_ANNOTATION sourceAnnotation;

    protected AbstractDataProviderArgumentProvider(DataConverter dataConverter) {
        this.dataConverter = dataConverter;
    }

    protected AbstractDataProviderArgumentProvider() {
        this(new DataConverter());
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note:</b> needs to be overridden because otherwise
     * {@link org.junit.jupiter.params.support.AnnotationConsumerInitializer} will not recognize it as {@code accept}
     * method which takes a single annotation argument. The implementation should be
     *
     * <pre>
     * <code>this.sourceAnnotation = sourceAnnotation;</code>
     * </pre>
     */
    @Override
    public abstract void accept(SOURCE_ANNOTATION sourceAnnotation);

    /**
     * Converts the given data for the given test method and converter context.
     *
     * @param testMethod the original test method for which the data is converted; never {@code null}
     * @param data the data to be converted; never {@code null}
     * @param context the converter context to be used to do the data conversion; never {@code null}
     * @return a {@link Stream} of properly converted arguments; never {@code null}
     * @throws NullPointerException if and only if one of the given arguments is {@code null}
     */
    protected Stream<? extends Arguments> convertData(Method testMethod, Object data, ConverterContext context) {
        checkNotNull(testMethod, "'testMethod' must not be null");
        checkNotNull(data, "'data' must not be null");
        checkNotNull(context, "'context' must not be null");

        return dataConverter.convert(data, testMethod.isVarArgs(), testMethod.getParameterTypes(), context).stream().map(Arguments::of);
    }
}
