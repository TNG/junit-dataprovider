package com.tngtech.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tngtech.junit.dataprovider.convert.DataConverter;

class AbstractDataProviderInvocationContextProviderTest {

    private AbstractDataProviderInvocationContextProvider<Annotation> underTest;

    private final Annotation testAnnotation = () -> Annotation.class;
    @SuppressWarnings("unchecked")
    private final Class<Annotation> testAnnotationClass = (Class<Annotation>) testAnnotation.getClass();

    @Mock
    private DataConverter dataConverter;
    @Mock
    private ExtensionContext extensionContext;

    private Method testMethod;

    @BeforeEach
    void setup() throws Exception {
        underTest = new AbstractDataProviderInvocationContextProvider<Annotation>(testAnnotationClass, dataConverter) {
            @Override
            protected Stream<TestTemplateInvocationContext> provideInvocationContexts(ExtensionContext extensionContext,
                    Annotation testAnnotation) {
                return null;
            }
        };
        testMethod = this.getClass().getDeclaredMethod("setup");

        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testProvideTestTemplateInvocationContextsShouldThrowProperExceptionIfAnnotationIsNotPresent() {
        // Given:
        when(extensionContext.getRequiredTestMethod()).thenReturn(testMethod);

        // When:
        Exception result = assertThrows(ExtensionConfigurationException.class,
                () -> underTest.provideTestTemplateInvocationContexts(extensionContext));

        // Then:
        assertThat(result).hasMessageMatching("Could not find annotation '.*' on test method '.*'\\.");
    }
}
