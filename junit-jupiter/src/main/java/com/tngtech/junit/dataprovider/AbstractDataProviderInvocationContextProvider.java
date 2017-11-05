package com.tngtech.junit.dataprovider;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.AnnotationUtils;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.convert.DataConverter;
import com.tngtech.junit.dataprovider.placeholder.ArgumentPlaceholder;
import com.tngtech.junit.dataprovider.placeholder.BasePlaceholder;
import com.tngtech.junit.dataprovider.placeholder.CanonicalClassNamePlaceholder;
import com.tngtech.junit.dataprovider.placeholder.CompleteMethodSignaturePlaceholder;
import com.tngtech.junit.dataprovider.placeholder.IndexPlaceholder;
import com.tngtech.junit.dataprovider.placeholder.NamedArgumentPlaceholder;
import com.tngtech.junit.dataprovider.placeholder.SimpleClassNamePlaceholder;
import com.tngtech.junit.dataprovider.placeholder.SimpleMethodNamePlaceholder;

/**
 * Abstract, internal invocation context provider to create {@link TestTemplateInvocationContext}s for dataprovider
 * tests.
 *
 * @param <TEST_ANNOTATION> annotation type used on test method for which this invocation context provider is concerned
 */
abstract class AbstractDataProviderInvocationContextProvider<TEST_ANNOTATION extends Annotation>
        implements TestTemplateInvocationContextProvider {

    /**
     * The {@link DataConverter} to be used to convert from supported return types of any dataprovider to {@link List}
     * {@code <}{@link Object}{@code []>} such that data can be further handled. Defaults to {@link DataConverter}.
     */
    private final DataConverter dataConverter;

    /**
     * The annotation class / type used on test methods to determine a dataprovider test.
     */
    private final Class<TEST_ANNOTATION> testAnnotationClass;

    protected AbstractDataProviderInvocationContextProvider(Class<TEST_ANNOTATION> testAnnotationClass,
            DataConverter dataConverter) {
        this.testAnnotationClass = testAnnotationClass;
        this.dataConverter = dataConverter;
    }

    protected AbstractDataProviderInvocationContextProvider(Class<TEST_ANNOTATION> testAnnotationClass) {
        this(testAnnotationClass, new DataConverter());
    }

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return context.getTestMethod().filter(m -> AnnotationSupport.isAnnotated(m, testAnnotationClass)).isPresent();
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        Method testMethod = context.getRequiredTestMethod();

        return AnnotationUtils.findAnnotation(testMethod, testAnnotationClass)
                .map(annotation -> provideInvocationContexts(context, annotation))
                .orElseThrow(() -> new ExtensionConfigurationException(String.format(
                        "Could not find annotation '%s' on test method '%s'.", testAnnotationClass, testMethod)));
    }

    /**
     * Method to provide annotation contexts in subclasses.
     *
     * @param extensionContext the extension context for the dataprovider test about to be invoked; never {@code null}
     * @param testAnnotation which annotates the test
     * @return a {@code Stream} of {@code TestTemplateInvocationContext} instances for the invocation of the
     *         dataprovider tests; never {@code null} or empty
     * @throws IllegalArgumentException if and only if no {@link TestTemplateInvocationContext} could be provided
     */
    protected abstract Stream<TestTemplateInvocationContext> provideInvocationContexts(ExtensionContext extensionContext,
            TEST_ANNOTATION testAnnotation);

    /**
     * @return the list of placeholders used to create the display name for each dataprovider test case
     */
    protected List<? extends BasePlaceholder> getDefaultPlaceholders() {
        List<BasePlaceholder> result = new ArrayList<>();
        result.add(new ArgumentPlaceholder());
        result.add(new CanonicalClassNamePlaceholder()); // must be before SimpleClassNamePlaceholder
        result.add(new CompleteMethodSignaturePlaceholder()); // must be before SimpleClassNamePlaceholder
        result.add(new IndexPlaceholder());
        result.add(new NamedArgumentPlaceholder());
        result.add(new SimpleClassNamePlaceholder());
        result.add(new SimpleMethodNamePlaceholder());
        return result;
    }

    /**
     * Converts the given data for the given test method and converter context.
     *
     * @param testMethod the original test method for which the data is converted; never {@code null}
     * @param data the data to be converted; never {@code null}
     * @param context the converter context to be used to do the data conversion; never {@code null}
     * @return a {@link Stream} of properly converted argument lists (= {@link List} of {@link Object}); never
     *         {@code null}
     * @throws NullPointerException if and only if one of the given arguments is {@code null}
     */
    protected Stream<List<Object>> convertData(Method testMethod, Object data, ConverterContext context) {
        checkNotNull(testMethod, "'testMethod' must not be null");
        checkNotNull(data, "'data' must not be null");
        checkNotNull(context, "'context' must not be null");

        return dataConverter.convert(data, testMethod.isVarArgs(), testMethod.getParameterTypes(), context).stream()
                .map(c -> Arrays.asList(c));
    }
}
