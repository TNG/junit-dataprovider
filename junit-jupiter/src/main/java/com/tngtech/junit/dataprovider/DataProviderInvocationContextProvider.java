package com.tngtech.junit.dataprovider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.convert.DataConverter;

/**
 * Abstract class which provides the default implementation for creating a custom dataprovider annotation that provides
 * the data directly within itself.
 *
 * @param <TEST_ANNOTATION> annotation type used to check support of this extension and create the invocation contexts
 *
 * @see DataProvider#value()
 * @see DataProviderExtension
 */
public abstract class DataProviderInvocationContextProvider<TEST_ANNOTATION extends Annotation>
        extends AbstractDataProviderInvocationContextProvider<TEST_ANNOTATION> {

    protected DataProviderInvocationContextProvider(Class<TEST_ANNOTATION> annotationClass,
            DataConverter dataConverter) {
        super(annotationClass, dataConverter);
    }

    protected DataProviderInvocationContextProvider(Class<TEST_ANNOTATION> annotationClass) {
        super(annotationClass);
    }

    @Override
    protected Stream<TestTemplateInvocationContext> provideInvocationContexts(ExtensionContext extensionContext,
            TEST_ANNOTATION testAnnotation) {
        Method testMethod = extensionContext.getRequiredTestMethod();
        return convertData(testMethod, getData(testAnnotation), getConverterContext(testAnnotation))
                .map(d -> new DataProviderInvocationContext(testMethod, d, getDisplayNameContext(testAnnotation)));
    }

    /**
     * @param annotation on the test method which is providing the test data; never {@code null}
     * @return the test data to be converted and used with the annotated test method; never {@code null}
     */
    protected abstract Object getData(TEST_ANNOTATION annotation);

    /**
     * @param annotation on the test method which is providing the converter context; never {@code null}
     * @return the converter context used to convert the data to be used with the annotated test method; never
     *         {@code null}
     */
    protected abstract ConverterContext getConverterContext(TEST_ANNOTATION annotation);

    /**
     * @param annotation on the test method which is used to determine the display name context; never {@code null}
     * @return the display name context used to create the display name of the test method; never {@code null}
     *
     * @see #getDefaultPlaceholders()
     */
    protected abstract DisplayNameContext getDisplayNameContext(TEST_ANNOTATION annotation);
}
