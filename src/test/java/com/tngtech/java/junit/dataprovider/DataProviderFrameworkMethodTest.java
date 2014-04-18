package com.tngtech.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class DataProviderFrameworkMethodTest {

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = NullPointerException.class)
    public void testDataProviderFrameworkMethodShouldThrowIllegalArgumentExceptionIfArrayParameterIsNull() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(anyMethod(), 0, (Object[]) null);

        // Then: expect exception
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = NullPointerException.class)
    public void testDataProviderFrameworkMethodShouldThrowIllegalArgumentExceptionIfParameterIsNull() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(anyMethod(), 0, (List<Object>) null);

        // Then: expect exception
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = IllegalArgumentException.class)
    public void testDataProviderFrameworkMethodShouldThrowIllegalArgumentExceptionIfArrayParameterIsEmpty() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(anyMethod(), 0, new Object[0]);

        // Then: expect exception
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = IllegalArgumentException.class)
    public void testDataProviderFrameworkMethodShouldThrowIllegalArgumentExceptionIfListParameterIsEmpty() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(anyMethod(), 0, new ArrayList<Object>());

        // Then: expect exception
    }

    @Test
    public void testDataProviderFrameworkMethodWithMethodIntArray() {
        // Given:
        final Object[] parameters = new Object[] { null, "1", 2L };

        // When:
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(anyMethod(), 1, parameters);

        // Then:
        assertThat(underTest).isNotNull();
        assertThat(underTest.parameters).isEqualTo(parameters);
    }

    @Test
    public void testDataProviderFrameworkMethodWithMethodIntList() {
        // Given:
        final List<Object> parameters = list(null, "1", 2L);

        // When:
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(anyMethod(), 1, parameters);

        // Then:
        assertThat(underTest).isNotNull();
        assertThat(underTest.parameters).isEqualTo(parameters.toArray());
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
    public void testGetNameShouldReturnSpecialHandlingForNullNull() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { null, null };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 4, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[4: <null>, <null>]");
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
    public void testGetNameShouldReturnSpecialHandlingForPrimitiveBooleanTypeArray() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new boolean[] { true, false } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 5, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[5: \\[true, false]]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForPrimitiveCharTypeArray() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new char[] { 'a', '0' } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 6, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[6: \\[a, 0]]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForPrimitiveIntTypeArray() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new int[] { 11, 2 } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 7, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[7: \\[11, 2]]");
    }

    @Test
    public void testGetNameShouldReturnSpecialHandlingForPrimitiveDoubleTypeArray() {
        // Given:
        Method method = anyMethod();
        final Object[] parameters = new Object[] { new double[] { .78, 3.15E2 } };

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 8, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[8: \\[0.78, 315.0]]");
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
    public void testGetNameShouldShowAllParametersFromListParametersString() {
        // Given:
        Method method = anyMethod();
        final List<Object> parameters = list(Integer.valueOf(44), "foo", BigDecimal.ONE);

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 1, parameters);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).matches(method.getName() + "\\[1: 44, foo, 1\\]");
    }

    @Test
    public void testHashCodeShouldBeEqualForEqualArrayObjects() {
        // Given:
        final Object[] params1 = new Object[] { "5", 6, 7L };
        final Object[] params2 = new Object[] { "5", 6, 7L };

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 3, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 3, params2);

        // When:
        int result = m1.hashCode();

        // Then:
        assertThat(result).isEqualTo(m2.hashCode());
    }

    @Test
    public void testHashCodeShouldBeEqualForEqualListObjects() {
        // Given:
        final List<Object> params1 = list("101", 102, 103L);
        final List<Object> params2 = list("101", 102, 103L);

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 3, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 3, params2);

        // When:
        int result = m1.hashCode();

        // Then:
        assertThat(result).isEqualTo(m2.hashCode());
    }

    @Test
    public void testEqualsShouldReturnTrueForEqualArrayObjects() {
        // Given:
        final Object[] params1 = new Object[] { null, "8", 9, 10L };
        final Object[] params2 = new Object[] { null, "8", 9, 10L };

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 4, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 4, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testEqualsShouldReturnTrueForEqualListObjects() {
        // Given:
        final List<Object> params1 = list(104L, 105, null, "106");
        final List<Object> params2 = list(104L, 105, null, "106");

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 4, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 4, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testEqualsShouldReturnTrueForEqualArrayAndListObjects() {
        // Given:
        final Object[] params1 = new Object[] { 107L, "108", null };
        final List<Object> params2 = list(107L, "108", null);

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 4, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 4, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testEqualsShouldReturnFalseForUnequalIndices() {
        // Given:
        final Object[] params1 = new Object[] { null, "11", 12, 13L };
        final Object[] params2 = new Object[] { null, "11", 12, 13L };

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 5, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 6, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testEqualsShouldReturnFalseForUnequalArrayParamValues() {
        // Given:
        final Object[] params1 = new Object[] { "test111", 222L, 333 };
        final Object[] params2 = new Object[] { "test111", 223L, 333 };

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 7, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 7, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testEqualsShouldReturnFalseForUnequalListParamValues() {
        // Given:
        final List<Object> params1 = list("bar5", 123L);
        final List<Object> params2 = list("bar5", 124L);

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 7, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 7, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testEqualsShouldReturnFalseForUnequalArrayParamTypes() {
        // Given:
        final Object[] params1 = new Object[] { 14, 25L, 36 };
        final Object[] params2 = new Object[] { "14", 25, 36L };

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(anyMethod(), 8, params1);
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(anyMethod(), 8, params2);

        // When:
        boolean result = m1.equals(m2);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testEqualsShouldReturnFalseForUnequalListParamTypes() {
        // Given:
        final List<Object> params1 = list(91, 92L, 93);
        final List<Object> params2 = list("91", 92, 93L);

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

    private static List<Object> list(Object... objects) {
        return Arrays.asList(objects);
    }
}
