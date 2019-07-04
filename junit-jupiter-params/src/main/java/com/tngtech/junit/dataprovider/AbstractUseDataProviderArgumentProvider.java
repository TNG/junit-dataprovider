package com.tngtech.junit.dataprovider;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;
import static com.tngtech.junit.dataprovider.resolver.DataProviderMethodResolverHelper.findDataProviderMethods;
import static org.junit.jupiter.engine.extension.ExtensionRegistry.createRegistryWithDefaultExtensions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.engine.config.DefaultJupiterConfiguration;
import org.junit.jupiter.engine.execution.ExecutableInvoker;
import org.junit.jupiter.engine.extension.ExtensionRegistry;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.platform.engine.ConfigurationParameters;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.convert.DataConverter;
import com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext;

/**
 * Abstract class which provides the default implementation for creating a custom dataprovider annotation that uses an
 * "external" dataprovider which provides the data.
 *
 * @param <SOURCE_ANNOTATION> annotation type used to provide the dataprovider and its data
 *
 * @see UseDataProvider
 * @see UseDataProviderArgumentProvider
 */
public abstract class AbstractUseDataProviderArgumentProvider<SOURCE_ANNOTATION extends Annotation, DATAPROVIDER_ANNOTATION extends Annotation>
        extends AbstractDataProviderArgumentProvider<SOURCE_ANNOTATION> {

    private static final ExecutableInvoker executableInvoker = new ExecutableInvoker();

    protected static final Namespace NAMESPACE_USE_DATAPROVIDER = Namespace
            .create(AbstractUseDataProviderArgumentProvider.class, "dataCache");

    private final Class<DATAPROVIDER_ANNOTATION> dataProviderAnnotationClass;

    protected AbstractUseDataProviderArgumentProvider(Class<DATAPROVIDER_ANNOTATION> dataProviderAnnotationClass,
            DataConverter dataConverter) {
        super(dataConverter);
        this.dataProviderAnnotationClass = dataProviderAnnotationClass;
    }

    protected AbstractUseDataProviderArgumentProvider(Class<DATAPROVIDER_ANNOTATION> dataProviderAnnotationClass) {
        super();
        this.dataProviderAnnotationClass = dataProviderAnnotationClass;
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Method testMethod = context.getRequiredTestMethod();

        DataProviderResolverContext resolverContext = getDataProviderResolverContext(context, sourceAnnotation);
        return findDataProviderMethods(resolverContext).stream().flatMap(dpm -> {
            DATAPROVIDER_ANNOTATION dataProviderAnnotation = dpm.getAnnotation(dataProviderAnnotationClass);
            boolean cacheDataProviderResult = cacheDataProviderResult(dataProviderAnnotation);

            Object data = invokeDataProviderMethodToRetrieveData(dpm, cacheDataProviderResult, context);
            return convertData(testMethod, data, getConverterContext(dataProviderAnnotation));
        });
    }

    /**
     * @param extensionContext the extension context for the dataprovider test about to be invoked; never {@code null}
     * @param testAnnotation which annotates the test; never {@code null}
     * @return the dataprovider resolver context used to resolve the dataprovider and its data
     */
    protected abstract DataProviderResolverContext getDataProviderResolverContext(ExtensionContext extensionContext,
            SOURCE_ANNOTATION testAnnotation);

    /**
     * @param dataProvider on the test method which is providing the converter context; never {@code null}
     * @return the converter context used to convert the data to be used with the annotated test method; never
     *         {@code null}
     */
    protected abstract ConverterContext getConverterContext(DATAPROVIDER_ANNOTATION dataProvider);

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
            ExtensionRegistry extensionRegistry = createRegistryWithDefaultExtensions(new DefaultJupiterConfiguration(emptyConfigurationParameters()));
            Object data = executableInvoker.invoke(dataProviderMethod, context.getTestInstance().orElse(null), context,
                    extensionRegistry);
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
