package com.tngtech.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.lang.reflect.Method;

import org.junit.Test;

public class DataProviderFrameworkMethodTest {

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = NullPointerException.class)
    public void testDataProviderFrameworkMethodShouldThrowIllegalArgumentExceptionIfParameterIsNull() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(anyMethod(), 0, null);

        // Then: expect exception
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = IllegalArgumentException.class)
    public void testDataProviderFrameworkMethodShouldThrowIllegalArgumentExceptionIfParameterIsEmpty() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(anyMethod(), 1, new Object[0]);

        // Then: expect exception
    }

    @Test
    public void testDataProviderFrameworkMethod() {
        // Given:
        final Object[] parameters = new Object[] { null, "1", 2L };

        // When:
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(anyMethod(), 10, parameters);

        // Then:
        assertThat(underTest).isNotNull();
        assertThat(underTest.parameters).isEqualTo(parameters);
    }

    @Test
    public void testGetNameShouldReturnParametersStringContainingSingleStringValueIfJustOneParameterIsGiven() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { 718 };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 20, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[20: 718]");
    }

    @Test
    public void testGetNameShouldReturnParamtersStringContainingMultipleValuesIfMultipleParametersAreGiven() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { 1024, "32", 128L };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 21, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[21: 1024, 32, 128\\]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForNull() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { null };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 30, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[30: <null>]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForNullNull() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { null, null };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 31, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[31: <null>, <null>]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForEmtpyString() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { "" };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 40, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[40: <empty string>]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForStringArray() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new String[] { "test" } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 50, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[50: \\[test]]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForPrimitiveBooleanTypeArray() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new boolean[] { true, false } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 52, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[52: \\[true, false]]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForPrimitiveCharTypeArray() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new char[] { 'a', '0' } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 54, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[54: \\[a, 0]]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForPrimitiveIntTypeArray() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new int[] { 11, 2 } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 56, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[56: \\[11, 2]]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForPrimitiveDoubleTypeArray() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new double[] { .78, 3.15E2 } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 58, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[58: \\[0.78, 315.0]]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForFurtherNestedArrays() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new Object[] { 1, new String[] { "a", "b", "c" } } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 60, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[60: \\[1, \\[a, b, c]]]");
    }

    @Test
    public void testEqualsShouldReturnTrueForEqualObjects() {
        // Given:
        final Object[] params1 = new Object[] { "str", 3, true };
        final Object[] params2 = new Object[] { "str", 3, true };

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 70, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 70, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testEqualsShouldReturnFalseForUnequalIndices() {
        // Given:
        final Object[] params1 = new Object[] { null, 'a', false };
        final Object[] params2 = new Object[] { null, 'a', false };

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 81, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 82, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testEqualsShouldReturnFalseForUnequalParamValues() {
        // Given:
        final Object[] params1 = new Object[] { "test", 4L, true };
        final Object[] params2 = new Object[] { "test", 5L, false };

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 83, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 83, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testEqualsShouldReturnFalseForUnequalParamTypes() {
        // Given:
        final Object[] params1 = new Object[] { 1, 22L, 333 };
        final Object[] params2 = new Object[] { "1", 22, 333L };

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 84, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 84, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testHashCodeShouldBeEqualForEqualObjects() {
        // Given:
        final Object[] params1 = new Object[] { 4.2, 't', false };
        final Object[] params2 = new Object[] { 4.2, 't', false };

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 90, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 90, params2);

        // When:
        int result = m1.hashCode();

        // Then:
        assertThat(result).isEqualTo(m2.hashCode());
    }

    private static Method anyMethod() {
        final Class<DataProviderFrameworkMethodTest> clazz = DataProviderFrameworkMethodTest.class;
        final String methodName = "anyMethod";

        try {
            return clazz.getDeclaredMethod(methodName);
        } catch (Exception e) {
            fail(String.format("No method with name '%s' found in %s", methodName, clazz));
            return null; // fool compiler
        }
    }
}
