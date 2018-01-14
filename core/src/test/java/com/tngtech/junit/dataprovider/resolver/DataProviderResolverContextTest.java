package com.tngtech.junit.dataprovider.resolver;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.CheckReturnValue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.junit.dataprovider.convert.SingleArgConverter;
import com.tngtech.junit.dataprovider.convert.StringConverter;
import com.tngtech.junit.dataprovider.testutils.Methods;

@RunWith(MockitoJUnitRunner.class)
public class DataProviderResolverContextTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final Method testMethod = Methods.anyMethod();
    @SuppressWarnings("unchecked")
    private final List<Class<? extends DataProviderMethodResolver>> resolverClasses = asList(
            DataProviderMethodResolver.class, DefaultDataProviderMethodResolver.class);
    private final ResolveStrategy resolveStrategy = ResolveStrategy.AGGREGATE_ALL_MATCHES;
    @SuppressWarnings("unchecked")
    private final List<Class<?>> locations = asList(this.getClass(), DataProviderResolverContext.class);
    private final Class<? extends Annotation> dataProviderAnnotationClass = CheckReturnValue.class;
    private final String dataProviderName = "dataProviderName";

    @Test
    public void testGenerateLocationsShouldThrowNullPointerExceptionIfTestClassIsNull() throws Exception {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'testClass' must not be null");

        // When:
        DataProviderResolverContext.generateLocations(null);

        // Then: expect exception
    }

    @Test
    public void testGenerateLocationsShouldReturnExplicitLocationsIfNotNull() throws Exception {
        // Given:
        Class<?>[] explicitLocations = { SingleArgConverter.class, StringConverter.class };

        // When:
        List<Class<?>> result = DataProviderResolverContext.generateLocations(this.getClass(), explicitLocations);

        // Then:
        assertThat(result).containsExactly(explicitLocations);
    }

    @Test
    public void testGenerateLocationsShouldReturnTestClassIfExplicitLocationsAreNull() throws Exception {
        // Given:
        Class<?>[] explicitLocations = null;

        // When:
        List<Class<?>> result = DataProviderResolverContext.generateLocations(this.getClass(), explicitLocations);

        // Then:
        assertThat(result).containsOnly(this.getClass());
    }

    @Test
    public void testGenerateLocationsShouldReturnTestClassIfExplicitLocationsAreEmpty() throws Exception {
        // Given:
        Class<?>[] explicitLocations = {};

        // When:
        List<Class<?>> result = DataProviderResolverContext.generateLocations(this.getClass(), explicitLocations);

        // Then:
        assertThat(result).containsOnly(this.getClass());
    }

    @Test
    public void testDataProviderResolverContextShouldThrowNullPointerExceptionIfTestMethodIsNull() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'testMethod' must not be null");

        // When:
        new DataProviderResolverContext(null, resolverClasses, resolveStrategy, locations, dataProviderAnnotationClass,
                dataProviderName);

        // Then: expect exception
    }

    @Test
    public void testDataProviderResolverContextShouldThrowNullPointerExceptionIfResolverClassesIsNull() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'resolverClasses' must not be null");

        // When:
        new DataProviderResolverContext(testMethod, null, resolveStrategy, locations, dataProviderAnnotationClass,
                dataProviderName);

        // Then: expect exception
    }

    @Test
    public void testDataProviderResolverContextShouldThrowNullPointerExceptionIfResolveStrategyIsNull() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'resolveStrategy' must not be null");

        // When:
        new DataProviderResolverContext(testMethod, resolverClasses, null, locations, dataProviderAnnotationClass,
                dataProviderName);

        // Then: expect exception
    }

    @Test
    public void testDataProviderResolverContextShouldThrowNullPointerExceptionIfLocationsIsNull() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'locations' must not be null");

        // When:
        new DataProviderResolverContext(testMethod, resolverClasses, resolveStrategy, null, dataProviderAnnotationClass,
                dataProviderName);

        // Then: expect exception
    }

    @Test
    public void testDataProviderResolverContextShouldThrowNullPointerExceptionIfDataProviderAnnotationClassIsNull() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'dataProviderAnnotationClass' must not be null");

        // When:
        new DataProviderResolverContext(testMethod, resolverClasses, resolveStrategy, locations, null,
                dataProviderName);

        // Then: expect exception
    }

    @Test
    public void testDataProviderResolverContextShouldThrowNullPointerExceptionIfDataProviderNameIsNull() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'dataProviderName' must not be null");

        // When:
        new DataProviderResolverContext(testMethod, resolverClasses, resolveStrategy, locations,
                dataProviderAnnotationClass, null);

        // Then: expect exception
    }

    @Test
    public void testDataProviderResolverContext() {
        // When:
        DataProviderResolverContext result = new DataProviderResolverContext(testMethod, resolverClasses,
                resolveStrategy, locations, dataProviderAnnotationClass, dataProviderName);

        // Then:
        assertThat(result).isNotNull();
        assertThat(result.getTestMethod()).isEqualTo(testMethod);
        assertThat(result.getResolverClasses()).isNotSameAs(resolverClasses).isEqualTo(resolverClasses);
        assertThat(result.getResolveStrategy()).isEqualTo(resolveStrategy);
        assertThat(result.getLocations()).isNotSameAs(locations).isEqualTo(locations);
        assertThat(result.getDataProviderAnnotationClass()).isEqualTo(dataProviderAnnotationClass);
        assertThat(result.getDataProviderName()).isEqualTo(dataProviderName);
    }

    @Test
    public void testDataProviderResolverContextShouldAddTestMethodsDeclaringClassToLocationsIfLocationsIsEmpty() {
        // Given:
        List<Class<?>> emptyLocations = emptyList();

        // When:
        DataProviderResolverContext result = new DataProviderResolverContext(testMethod, resolverClasses,
                resolveStrategy, emptyLocations, dataProviderAnnotationClass, dataProviderName);

        // Then:
        assertThat(result.getLocations()).containsOnly(testMethod.getDeclaringClass());
    }

    @Test
    public void testUseDataProviderNameConventionShouldReturnFalseIfDataProviderNameIsSet() {
        // Given:
        final String dataProviderName = "dataProviderName";

        DataProviderResolverContext underTest = new DataProviderResolverContext(testMethod, resolverClasses,
                resolveStrategy, locations, dataProviderAnnotationClass, dataProviderName);

        // When:
        boolean result = underTest.useDataProviderNameConvention();

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testUseDataProviderNameConventionShouldReturnTrueIfDatProviderIsUseConvention() {
        // Given:
        final String dataProviderName = DataProviderResolverContext.METHOD_NAME_TO_USE_CONVENTION;

        DataProviderResolverContext underTest = new DataProviderResolverContext(testMethod, resolverClasses,
                resolveStrategy, locations, dataProviderAnnotationClass, dataProviderName);

        // When:
        boolean result = underTest.useDataProviderNameConvention();

        // Then:
        assertThat(result).isTrue();
    }

}
