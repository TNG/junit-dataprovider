package com.tngtech.junit.dataprovider.resolver;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.assertj.core.api.Condition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.tngtech.junit.dataprovider.testutils.Methods;

public class DefaultDataProviderMethodResolverTest {

    @SuppressWarnings("deprecation")
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification = "Mockito rule needs no further configuration")
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Spy
    private DefaultDataProviderMethodResolver underTest;
    @Mock
    private DataProviderResolverContext context;

    private final Method method = Methods.anyMethod();

    @Test
    public void testResolveShouldThrowNullPointerExceptionIfContextIsNull() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'context' must not be null");

        // When:
        underTest.resolve(null);

        // Then: expect exception
    }

    @Test
    public void testResolveShouldReturnEmptyListForNotFoundDataProviderMethods() {
        // Given:
        doReturn(Collections.<Method>emptyList()).when(underTest).findAnnotatedMethods(
                ArgumentMatchers.<List<Class<?>>>any(), ArgumentMatchers.<Class<? extends Annotation>>any());

        // When:
        List<Method> result = underTest.resolve(context);

        // Then:
        assertThat(result).isEmpty();
    }

    @Test
    public void testResolveShouldReturnListContainingExplicitelySpecifiedDataProviderMethod() {
        // Given:
        final Method method2 = Methods.getMethod(this.getClass(), "dataProviderMethod");
        final Method method3 = Methods.anyMethod();

        doReturn(asList(method, method2, method3)).when(underTest).findAnnotatedMethods(
                ArgumentMatchers.<List<Class<?>>>any(), ArgumentMatchers.<Class<? extends Annotation>>any());

        when(context.useDataProviderNameConvention()).thenReturn(false);
        when(context.getDataProviderName()).thenReturn("dataProviderMethod");

        // When:
        List<Method> result = underTest.resolve(context);

        // Then:
        assertThat(result).containsOnly(method2);
    }

    @SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT", justification = "doReturn causes this but required due to spy")
    @Test
    public void testResolveShouldReturnListContainingOnlyConventionMatchingDataProviderMethods() {
        // Given:
        final Method method2 = Methods.anyMethod();
        final Method method3 = Methods.anyMethod();

        doReturn(asList(method, method2, method3)).when(underTest).findAnnotatedMethods(
                ArgumentMatchers.<List<Class<?>>>any(), ArgumentMatchers.<Class<? extends Annotation>>any());
        doReturn(false).doReturn(true).doReturn(true).when(underTest).isMatchingNameConvention(any(String.class),
                any(String.class));

        when(context.useDataProviderNameConvention()).thenReturn(true);
        when(context.getTestMethod()).thenReturn(Methods.anyMethod());

        // When:
        List<Method> result = underTest.resolve(context);

        // Then:
        assertThat(result).containsOnly(method2, method3);
    }

    @Test
    public void testFindAnnotatedMethodsShouldNotShadowedMethodsHavingAnnotation() {
        // Given:
        final List<Class<?>> locations = Collections.<Class<?>>singletonList(ShadowingTestChild.class);

        // When:
        List<Method> result = underTest.findAnnotatedMethods(locations, TestAnnotation.class);

        // Then:
        // @formatter:off
        assertThat(result).hasSize(7)
                .has(name("privateWithAnnotation"), atIndex(0))
                .has(name("notShadowedDueToParameterLength"), atIndex(1))
                .has(name("notShadowedDueToParameterTypes"), atIndex(2))
                .has(name("shadowed"), atIndex(3))
                .has(name("notShadowedDueToParameterLength"), atIndex(4))
                .has(name("notShadowedDueToParameterTypes"), atIndex(5))
                .has(name("shadowedButChildHasNoAnnotation"), atIndex(6))
            ;
        // @formatter:on
    }

    @Test
    public void testFindAnnotatedMethodsShouldReturnMethodsForAllLocations() {
        // Given:
        final List<Class<?>> locations = Arrays.<Class<?>>asList(ShadowingTestChild.class, ShadowingTestParent.class);

        // When:
        List<Method> result = underTest.findAnnotatedMethods(locations, TestAnnotation.class);

        // Then:
        assertThat(result).hasSize(11);
    }

    @Test
    public void testIsMatchingNameConventionShouldReturnFalseForNamesNotStickToSupportedConventions() {
        // Given:
        final String testMethodName = "testMethod";
        final String dataProviderMethodName = "notMatchingDataProviderMethod";

        // When:
        boolean result = underTest.isMatchingNameConvention(testMethodName, dataProviderMethodName);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testIsMatchingNameConventionShouldReturnTrueIfDataProviderMethodHavingSameNameAsTestMethod() {
        // Given:
        final String testMethodName = "testMethod";

        // When:
        boolean result = underTest.isMatchingNameConvention(testMethodName, testMethodName);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testIsMatchingNameConventionShouldReturnTrueIfDataProviderMethodHavingDataProviderPrefixInsteadOfTest() {
        // Given:
        final String testMethodName = "testMethod";
        final String dataProviderMethodName = "dataProviderMethod";

        // When:
        boolean result = underTest.isMatchingNameConvention(testMethodName, dataProviderMethodName);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testIsMatchingNameConventionShouldReturnTrueIfDataProviderMethodHavingDataPrefixInsteadOfTest() {
        // Given:
        final String testMethodName = "testMethod";
        final String dataProviderMethodName = "dataMethod";

        // When:
        boolean result = underTest.isMatchingNameConvention(testMethodName, dataProviderMethodName);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testIsMatchingNameConventionShouldReturnTrueIfDataProviderMethodHavingDataProviderPrefix() {
        // Given:
        final String testMethodName = "testMethod";
        final String dataProviderMethodName = "dataProviderTestMethod";

        // When:
        boolean result = underTest.isMatchingNameConvention(testMethodName, dataProviderMethodName);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testIsMatchingNameConventionShouldReturnTrueIfDataProviderMethodHavingDataPrefix() {
        // Given:
        final String testMethodName = "testMethod";
        final String dataProviderMethodName = "dataTestMethod";

        // When:
        boolean result = underTest.isMatchingNameConvention(testMethodName, dataProviderMethodName);

        // Then:
        assertThat(result).isTrue();
    }

    // -- test data and helper methods ---------------------------------------------------------------------------------

    @Retention(RetentionPolicy.RUNTIME)
    private @interface TestAnnotation {
        // annotation exclusively used for test
    }

    void dataProviderMethod() {
        // method used in tests via reflection
    }

    @SuppressWarnings("unused")
    private static class ShadowingTestParent {
        @TestAnnotation
        protected void notShadowedDueToParameterLength(int i, Double d) {
            // method used in tests via reflection
        }

        @TestAnnotation
        void notShadowedDueToParameterTypes(Integer i) {
            // method used in tests via reflection
        }

        @TestAnnotation
        public void shadowed(String s, Long l) {
            // method used in tests via reflection
        }

        @TestAnnotation
        protected void shadowedButChildHasNoAnnotation(Double d) {
            // method used in tests via reflection
        }
    }

    @SuppressWarnings("unused")
    private static class ShadowingTestChild extends ShadowingTestParent {
        @TestAnnotation
        protected void notShadowedDueToParameterLength(int i) {
            // method used in tests via reflection
        }

        @TestAnnotation
        void notShadowedDueToParameterTypes(long l) {
            // method used in tests via reflection
        }

        @Override
        @TestAnnotation
        public void shadowed(String s, Long l) {
            // method used in tests via reflection
        }

        @Override
        protected void shadowedButChildHasNoAnnotation(Double d) {
            // method used in tests via reflection
        }

        @TestAnnotation
        protected void privateWithAnnotation() {
            // method used in tests via reflection
        }
    }

    private Condition<Method> name(final String methodName) {
        return new Condition<Method>() {
            @Override
            public boolean matches(Method value) {
                return methodName.equals(value.getName());
            }
        };
    }

}
