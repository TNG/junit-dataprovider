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

import static com.tngtech.junit.dataprovider.Preconditions.checkArgument;
import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;
import static com.tngtech.junit.dataprovider.resolver.DataProviderMethodResolverHelper.findDataProviderMethods;
import static org.junit.jupiter.engine.extension.MutableExtensionRegistry.createRegistryWithDefaultExtensions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.engine.config.DefaultJupiterConfiguration;
import org.junit.jupiter.engine.execution.ExecutableInvoker;
import org.junit.jupiter.engine.extension.ExtensionRegistry;
import org.junit.platform.engine.ConfigurationParameters;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.convert.DataConverter;
import com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext;

/**
 * Abstract class which provides the default implementation for creating a custom dataprovider annotation that uses an
 * "external" dataprovider which provides the data.
 *
 * @param <TEST_ANNOTATION> annotation type used to check support of this extension and create the invocation contexts
 *
 * @see UseDataProvider
 * @see UseDataProviderExtension
 */
public abstract class UseDataProviderInvocationContextProvider<TEST_ANNOTATION extends Annotation, DATAPROVIDER_ANNOTATION extends Annotation>
        extends AbstractDataProviderInvocationContextProvider<TEST_ANNOTATION> {

    private static final ExecutableInvoker executableInvoker = new ExecutableInvoker();

    protected static final Namespace NAMESPACE_USE_DATAPROVIDER = Namespace
            .create(UseDataProviderInvocationContextProvider.class, "dataCache");

    private final Class<DATAPROVIDER_ANNOTATION> dataProviderAnnotationClass;

    protected UseDataProviderInvocationContextProvider(Class<TEST_ANNOTATION> annotationClass,
            Class<DATAPROVIDER_ANNOTATION> dataProviderAnnotationClass, DataConverter dataConverter) {
        super(annotationClass, dataConverter);
        this.dataProviderAnnotationClass = dataProviderAnnotationClass;
    }

    protected UseDataProviderInvocationContextProvider(Class<TEST_ANNOTATION> annotationClass,
            Class<DATAPROVIDER_ANNOTATION> dataProviderAnnotationClass) {
        super(annotationClass);
        this.dataProviderAnnotationClass = dataProviderAnnotationClass;
    }

    @Override
    protected Stream<TestTemplateInvocationContext> provideInvocationContexts(ExtensionContext context,
            TEST_ANNOTATION annotation) {
        Method testMethod = context.getRequiredTestMethod();

        DataProviderResolverContext dataProviderResolverContext = getDataProviderResolverContext(context, annotation);
        List<Method> dataProviderMethods = findDataProviderMethods(dataProviderResolverContext);

        checkArgument(dataProviderMethods.size() > 0,
                String.format("Could not find a dataprovider for test '%s' using resolvers '%s'.", testMethod,
                        dataProviderResolverContext.getResolverClasses()));

        return dataProviderMethods.stream().flatMap(dpm -> {
            DATAPROVIDER_ANNOTATION dataProviderAnnotation = dpm.getAnnotation(dataProviderAnnotationClass);
            boolean cacheDataProviderResult = cacheDataProviderResult(dataProviderAnnotation);

            Object data = invokeDataProviderMethodToRetrieveData(dpm, cacheDataProviderResult, context);

            return convertData(testMethod, data, getConverterContext(dataProviderAnnotation))
                    .map(d -> (TestTemplateInvocationContext) new DataProviderInvocationContext(testMethod, d,
                            getDisplayNameContext(dataProviderAnnotation)));
        });
    }

    /**
     * @param extensionContext the extension context for the dataprovider test about to be invoked; never {@code null}
     * @param testAnnotation which annotates the test; never {@code null}
     * @return the dataprovider resolver context used to resolve the dataprovider and its data; never {@code null}
     */
    protected abstract DataProviderResolverContext getDataProviderResolverContext(ExtensionContext extensionContext,
            TEST_ANNOTATION testAnnotation);

    /**
     * @param dataProviderAnnotation on the test method which is providing the converter context; never {@code null}
     * @return the converter context used to convert the data to be used with the annotated test method; never
     *         {@code null}
     */
    protected abstract ConverterContext getConverterContext(DATAPROVIDER_ANNOTATION dataProviderAnnotation);

    /**
     * @param dataProviderAnnotation on the test method which is providing the converter context; never {@code null}
     * @return {@code true} if and only if dataprovider result should be cached or evaluated otherwise. Defaults to
     *         {@code true} for backwards compatibility reasons.
     * @see DataProvider#cache()
     */
    protected boolean cacheDataProviderResult(
            @SuppressWarnings("unused") DATAPROVIDER_ANNOTATION dataProviderAnnotation) {
        return true;
    }

    /**
     * @param dataProviderAnnotation on the test method which is used to determine the display name context; never
     *            {@code null}
     * @return the display name context used to create the display name of the test method; never {@code null}
     *
     * @see #getDefaultPlaceholders()
     */
    protected abstract DisplayNameContext getDisplayNameContext(DATAPROVIDER_ANNOTATION dataProviderAnnotation);

    /**
     * Retrieves the test data from given dataprovider method.
     *
     * @param dataProviderMethod the dataprovider method that gives the parameters; never {@code null}
     * @param context the execution context to use to create a {@link TestInfo} if required; never {@code null}
     *
     * @return a list of methods, each method bound to a parameter combination returned by the dataprovider
     * @throws NullPointerException if and only if one of the given arguments is {@code null}
     *
     * @deprecated available for backwards compatibility, use new
     *             {@link #invokeDataProviderMethodToRetrieveData(Method, boolean, ExtensionContext)} instead
     * @see #invokeDataProviderMethodToRetrieveData(Method, boolean, ExtensionContext)
     */
    @Deprecated
    protected Object invokeDataProviderMethodToRetrieveData(Method dataProviderMethod, ExtensionContext context) {
        return invokeDataProviderMethodToRetrieveData(dataProviderMethod, true, context);
    }

    /**
     * Retrieves the test data from given dataprovider method.
     *
     * @param dataProviderMethod the dataprovider method that gives the parameters; never {@code null}
     * @param cacheDataProviderResult determines if the dataprovider result should be cached using
     *            {@code dataProviderMethod} as key
     * @param context the execution context to use to create a {@link TestInfo} if required; never {@code null}
     *
     * @return a list of methods, each method bound to a parameter combination returned by the dataprovider
     * @throws NullPointerException if and only if one of the given arguments is {@code null}
     */
    protected Object invokeDataProviderMethodToRetrieveData(Method dataProviderMethod, boolean cacheDataProviderResult,
            ExtensionContext context) {
        checkNotNull(dataProviderMethod, "'dataProviderMethod' must not be null");
        checkNotNull(context, "'context' must not be null");

        Store store = context.getRoot().getStore(NAMESPACE_USE_DATAPROVIDER);

        Object cached = store.get(dataProviderMethod);
        if (cached != null) {
            return cached;
        }
        try {
            // TODO how to not require junit-jupiter-engine dependency and reuse already existing ExtensionRegistry?
            ExtensionRegistry extensionRegistry = createRegistryWithDefaultExtensions(
                    new DefaultJupiterConfiguration(emptyConfigurationParameters()));
            Object data = executableInvoker.invoke(dataProviderMethod, context.getTestInstance().orElse(null), context,
                    extensionRegistry, InvocationInterceptor::interceptTestFactoryMethod);
            if (cacheDataProviderResult) {
                store.put(dataProviderMethod, data);
            }
            return data;

        } catch (Exception e) {
            throw new ParameterResolutionException(
                    String.format("Exception while invoking dataprovider method '%s': %s", dataProviderMethod.getName(),
                            e.getMessage()),
                    e);
        }
    }

    private ConfigurationParameters emptyConfigurationParameters() {
        return new ConfigurationParameters() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public Optional<Boolean> getBoolean(String key) {
                return Optional.empty();
            }

            @Override
            public Optional<String> get(String key) {
                return Optional.empty();
            }
        };
    }
}
