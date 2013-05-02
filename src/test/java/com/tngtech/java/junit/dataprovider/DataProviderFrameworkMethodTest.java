package com.tngtech.java.junit.dataprovider;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

import java.lang.reflect.Method;

import org.junit.Test;

public class DataProviderFrameworkMethodTest {

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = IllegalArgumentException.class)
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
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(anyMethod(), 0, new Object[0]);

        // Then: expect exception
    }

    @Test
    public void testDataProviderFrameworkMethod() {

        // Given:
        final Object[] parameters = new Object[] { null, "1", 2L };

        // When:
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(anyMethod(), 1, parameters);

        // Then:
        assertThat(underTest).isNotNull();
        assertThat(underTest.parameters).isEqualTo(parameters);
    }

    @Test
    public void testGetNameShouldReturnParametersStringContainingSingleStringValueIfJustOneParameterIsGiven() {

        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { 718 };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 2, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[2: 718]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForNull() {

        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { null };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 3, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[3: <null>]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForEmtpyString() {

        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { "" };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 4, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[4: <empty string>]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForArray() {

        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new String[] { "test" } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 4, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[4: \\[test]]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForPrimitiveTypeArray() {

        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new int[] { 1, 2 } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 4, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[4: \\[1, 2]]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForArrayInArray() {

        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new Object[] { 1, new String[] { "a", "b", "c" } } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 4, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[4: \\[1, \\[a, b, c]]]");
    }

    @Test
    public void testGetNameShouldShowAllParametersFromParametersString() {

        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { 1024, "32", 128L };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 1, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[1: 1024, 32, 128\\]");
    }

    @Test
    public void testHashCodeShouldBeEqualForEqualObjects() {

        final Object[] params1 = new Object[] { "5", 6, 7L };
        final Object[] params2 = new Object[] { "5", 6, 7L };

        // Given:

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 3, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 3, params2);

        // When:
        int result = m1.hashCode();

        // Then:
        assertThat(result).isEqualTo(m2.hashCode());
    }

    @Test
    public void testEqualsShouldReturnTrueForEqualObjects() {

        final Object[] params1 = new Object[] { null, "8", 9, 10L };
        final Object[] params2 = new Object[] { null, "8", 9, 10L };

        // Given:

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 4, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 4, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testEqualsShouldReturnFalseForUnequalIndices() {

        final Object[] params1 = new Object[] { null, "11", 12, 13L };
        final Object[] params2 = new Object[] { null, "11", 12, 13L };

        // Given:
        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 5, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 6, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testEqualsShouldReturnFalseForUnequalParamValues() {

        final Object[] params1 = new Object[] { "test111", 222L, 333 };
        final Object[] params2 = new Object[] { "test111", 223L, 333 };

        // Given:

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 7, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 7, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testEqualsShouldReturnFalseForUnequalParamTypes() {

        final Object[] params1 = new Object[] { 14, 25L, 36 };
        final Object[] params2 = new Object[] { "14", 25, 36L };

        // Given:

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 8, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 8, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isFalse();
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
