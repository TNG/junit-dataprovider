package com.tngtech.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.convert.DataConverter;
import com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext;

public class AbstractUseDataProviderArgumentProviderTest {

    private AbstractUseDataProviderArgumentProvider<Annotation, Annotation> underTest;

    private final Annotation dataProviderAnnotation = new Annotation() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return Annotation.class;
        }
    };
    @SuppressWarnings("unchecked")
    private final Class<Annotation> dataProviderAnnotationClass = (Class<Annotation>) dataProviderAnnotation.getClass();

    @Mock
    private DataConverter dataConverter;
    @Mock
    private ExtensionContext extensionContext;
    @Mock
    private DataProviderResolverContext dataProviderResolverContext;
    @Mock
    private ConverterContext converterContext;
    @Mock
    private Store store;

    @BeforeEach
    void setup() throws Exception {
        underTest = new AbstractUseDataProviderArgumentProvider<Annotation, Annotation>(dataProviderAnnotationClass,
                dataConverter) {
            @Override
            public void accept(Annotation sourceAnnotation) {
                // nothing to do
            }

            @Override
            protected DataProviderResolverContext getDataProviderResolverContext(ExtensionContext extensionContext,
                    Annotation testAnnotation) {
                return dataProviderResolverContext;
            }

            @Override
            protected ConverterContext getConverterContext(Annotation dataProvider) {
                return converterContext;
            }
        };

        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testInvokeDataProviderMethodToRetrieveDataShouldThrowParameterResolutionExceptionIfDataProviderInvocationThrows()
            throws Exception {
        // Given:
        Method dataProviderMethod = this.getClass().getDeclaredMethod(
                "testInvokeDataProviderMethodToRetrieveDataShouldThrowParameterResolutionExceptionIfDataProviderInvocationThrows");

        when(extensionContext.getRoot()).thenReturn(extensionContext);
        when(extensionContext.getStore(any(Namespace.class))).thenReturn(store);

        // When:
        Exception result = assertThrows(ParameterResolutionException.class,
                () -> underTest.invokeDataProviderMethodToRetrieveData(dataProviderMethod, extensionContext));

        // Then:
        assertThat(result).hasMessageMatching("Exception while invoking dataprovider method '.*': .*");
    }
}
