package com.tngtech.junit.dataprovider.resolver;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.tngtech.junit.dataprovider.testutils.Methods;

public class DataProviderMethodResolverHelperTest {

    private static class TestResolver implements DataProviderMethodResolver {

        private static List<DataProviderResolverContext> usedContexts;
        private static List<Method> methods;

        private TestResolver() {
        }

        @Override
        public List<Method> resolve(DataProviderResolverContext context) {
            usedContexts.add(context);
            return methods;
        }
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private DataProviderResolverContext context;

    @Before
    public void setup() {
        TestResolver.methods = new ArrayList<Method>();
        TestResolver.usedContexts = new ArrayList<DataProviderResolverContext>();
    }

    @Test
    public void testFindDataProviderMethodsShouldThrowNullPointerExceptionIfContextIsNull() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'context' must not be null");

        // When:
        DataProviderMethodResolverHelper.findDataProviderMethods(null);

        // Then: expect exception
    }

    @Test
    public void testFindDataProviderMethodsShouldReturnEmptyListIfContextDoesNotContainAnyResolver() {
        // Given:
        when(context.getResolverClasses())
                .thenReturn(Collections.<Class<? extends DataProviderMethodResolver>>emptyList());

        // When:
        List<Method> result = DataProviderMethodResolverHelper.findDataProviderMethods(context);

        // Then:
        assertThat(result).isEmpty();
    }

    @Test
    public void testFindDataProviderMethodsShouldReturnEmptyListIfSingleResolverReturnsEmptyList() {
        // Given:
        @SuppressWarnings("unchecked")
        List<Class<? extends DataProviderMethodResolver>> resolverClasses = Arrays
                .<Class<? extends DataProviderMethodResolver>>asList(TestResolver.class);
        when(context.getResolverClasses()).thenReturn(resolverClasses);

        TestResolver.methods = emptyList();

        // When:
        List<Method> result = DataProviderMethodResolverHelper.findDataProviderMethods(context);

        // Then:
        assertThat(result).isEmpty();
        assertThat(TestResolver.usedContexts).containsOnly(context);
    }

    @Test
    public void testFindDataProviderMethodsShouldReturnFirstMatchIfMutlipleResolverReturnNonEmptyList() {
        // Given:
        @SuppressWarnings("unchecked")
        final List<Class<? extends DataProviderMethodResolver>> resolverClasses = Arrays
                .<Class<? extends DataProviderMethodResolver>>asList(TestResolver.class, TestResolver.class);
        final Method method = Methods.anyMethod();

        when(context.getResolverClasses()).thenReturn(resolverClasses);
        when(context.getResolveStrategy()).thenReturn(ResolveStrategy.UNTIL_FIRST_MATCH);

        TestResolver.methods = asList(method);

        // When:
        List<Method> result = DataProviderMethodResolverHelper.findDataProviderMethods(context);

        // Then:
        assertThat(result).containsOnly(method);
        assertThat(TestResolver.usedContexts).containsOnly(context);
    }

    @Test
    public void testFindDataProviderMethodsShouldReturnFirstMatchEvenIfMutlipleResolverReturnNonEmptyList() {
        // Given:
        @SuppressWarnings("unchecked")
        final List<Class<? extends DataProviderMethodResolver>> resolverClasses = Arrays
                .<Class<? extends DataProviderMethodResolver>>asList(TestResolver.class, TestResolver.class);

        when(context.getResolverClasses()).thenReturn(resolverClasses);
        when(context.getResolveStrategy()).thenReturn(ResolveStrategy.UNTIL_FIRST_MATCH);

        TestResolver.methods = emptyList();

        // When:
        List<Method> result = DataProviderMethodResolverHelper.findDataProviderMethods(context);

        // Then:
        assertThat(result).isEmpty();
        assertThat(TestResolver.usedContexts).containsOnly(context, context);
    }

    @Test
    public void testFindDataProviderMethodsShouldReturnAllMatchesIfMutlipleResolverReturnNonEmptyList() {
        // Given:
        @SuppressWarnings("unchecked")
        final List<Class<? extends DataProviderMethodResolver>> resolverClasses = Arrays
                .<Class<? extends DataProviderMethodResolver>>asList(TestResolver.class, TestResolver.class);
        final Method method = Methods.anyMethod();

        when(context.getResolverClasses()).thenReturn(resolverClasses);
        when(context.getResolveStrategy()).thenReturn(ResolveStrategy.AGGREGATE_ALL_MATCHES);

        TestResolver.methods = asList(method);

        // When:
        List<Method> result = DataProviderMethodResolverHelper.findDataProviderMethods(context);

        // Then:
        assertThat(result).containsOnly(method, method);
        assertThat(TestResolver.usedContexts).containsOnly(context, context);
    }

    @Test
    public void testFindDataProviderMethodsShouldThrowIllegalArgumentExceptionIfResolverClassHasNoDefaultConstructorExists() {
        // Given:
        @SuppressWarnings("unchecked")
        final List<Class<? extends DataProviderMethodResolver>> resolverClasses = Arrays
                .<Class<? extends DataProviderMethodResolver>>asList(NoDefaultConstructor.class);

        when(context.getResolverClasses()).thenReturn(resolverClasses);

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Could not find default constructor to instantiate");

        // When:
        DataProviderMethodResolverHelper.findDataProviderMethods(context);

        // Then: expect exception
    }

    @Test
    public void testFindDataProviderMethodsShouldThrowIllegalArgumentExceptionIfResolverClassHasIsAbstract() {
        // Given:
        @SuppressWarnings("unchecked")
        final List<Class<? extends DataProviderMethodResolver>> resolverClasses = Arrays
                .<Class<? extends DataProviderMethodResolver>>asList(AbstractClass.class);

        when(context.getResolverClasses()).thenReturn(resolverClasses);

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Could not instantiate");
        expectedException.expectMessage("using default constructor");

        // When:
        DataProviderMethodResolverHelper.findDataProviderMethods(context);

        // Then: expect exception
    }

    @Test
    public void testFindDataProviderMethodsShouldThrowIllegalArgumentExceptionIfResolverClassesDefaultConstructorThrowsException() {
        // Given:
        @SuppressWarnings("unchecked")
        final List<Class<? extends DataProviderMethodResolver>> resolverClasses = Arrays
                .<Class<? extends DataProviderMethodResolver>>asList(ExceptionInDefaultConstructor.class);

        when(context.getResolverClasses()).thenReturn(resolverClasses);

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("default constructor of ");
        expectedException.expectMessage("has thrown an exception");

        // When:
        DataProviderMethodResolverHelper.findDataProviderMethods(context);

        // Then: expect exception
    }

    // -- helper classes to test with ----------------------------------------------------------------------------------

    private static class NoDefaultConstructor implements DataProviderMethodResolver {
        @SuppressWarnings("unused")
        public NoDefaultConstructor(String a) {
            // unused
        }

        @Override
        public List<Method> resolve(DataProviderResolverContext context) {
            return null;
        }
    }

    private static abstract class AbstractClass implements DataProviderMethodResolver {
        // unused
    }

    private static class ExceptionInDefaultConstructor implements DataProviderMethodResolver {
        @SuppressWarnings("unused")
        public ExceptionInDefaultConstructor() {
            throw new NumberFormatException();
        }

        @Override
        public List<Method> resolve(DataProviderResolverContext context) {
            return null;
        }
    }
}