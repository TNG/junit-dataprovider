package com.tngtech.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.internal.placeholder.BasePlaceholder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@RunWith(MockitoJUnitRunner.class)
public class DataProviderFrameworkMethodTest extends BaseTest {

    @Mock
    private BasePlaceholder placeholder;

    private final Method method = anyMethod();

    @After
    public void tearDown() {
        Placeholders.reset();
    }

    @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = NullPointerException.class)
    public void testDataProviderFrameworkMethodShouldThrowNullPointerExceptionIfParameterIsNull() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 0, null, "%m");

        // Then: expect exception
    }

    @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = NullPointerException.class)
    public void testDataProviderFrameworkMethodShouldThrowNullPointerExceptionIfDataProviderIsNull() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 0, new Object[] { 1 }, null);

        // Then: expect exception
    }

    @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = IllegalArgumentException.class)
    public void testDataProviderFrameworkMethodShouldThrowIllegalArgumentExceptionIfParameterIsEmpty() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 1, new Object[0], "%m");

        // Then: expect exception
    }

    @Test
    public void testDataProviderFrameworkMethod() {
        // Given:
        final int idx = 10;
        final Object[] parameters = new Object[] { null, "1", 2L };
        final String nameFormat = "%cm";

        // When:
        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, idx, parameters, nameFormat);

        // Then:
        assertThat(underTest).isNotNull();
        assertThat(underTest.getMethod()).isEqualTo(method);
        assertThat(underTest.idx).isEqualTo(idx);
        assertThat(underTest.parameters).isEqualTo(parameters);
        assertThat(underTest.nameFormat).isEqualTo(nameFormat);
    }

    @Test
    public void testGetNameShouldCallPlaceholderSetContextAndProcess() {
        // Given:
        final Object[] parameters = new Object[] { 718, "718" };

        Placeholders.all().clear();
        Placeholders.all().add(placeholder);

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 20, parameters, "%m");

        // When:
        underTest.getName();

        // Then:
        verify(placeholder).setContext(method, 20, parameters);
        verify(placeholder).process("%m");
        verifyNoMoreInteractions(placeholder);
    }

    @Test
    public void testGetNameShouldCallAllPlaceholdersProcessInOrder() {
        // Given:
        final Object[] parameters = new Object[] { 719, "719" };

        BasePlaceholder placeholder2 = mock(BasePlaceholder.class);
        BasePlaceholder placeholder3 = mock(BasePlaceholder.class);

        Placeholders.all().clear();
        Placeholders.all().add(placeholder);
        Placeholders.all().add(placeholder2);
        Placeholders.all().add(placeholder3);

        when(placeholder.process(any(String.class))).thenReturn("%cm2");
        when(placeholder2.process(any(String.class))).thenReturn("%cm3");
        when(placeholder3.process(any(String.class))).thenReturn("%cm4");

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 21, parameters, "%cm1");

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).isEqualTo("%cm4");

        InOrder inOrder = inOrder(placeholder, placeholder2, placeholder3);
        inOrder.verify(placeholder).setContext(method, 21, parameters);
        inOrder.verify(placeholder).process("%cm1");
        inOrder.verify(placeholder2).setContext(method, 21, parameters);
        inOrder.verify(placeholder2).process("%cm2");
        inOrder.verify(placeholder3).setContext(method, 21, parameters);
        inOrder.verify(placeholder3).process("%cm3");
        verifyNoMoreInteractions(placeholder, placeholder2, placeholder3);
    }

    @Test
    public void testGetNameShouldRetrunResultOfProcess() {
        // Given:
        final Method method = getMethod("testGetNameShouldRetrunResultOfProcess");
        final Object[] parameters = new Object[] { 720, "720" };
        final String nameFormat = "%m[%i: %p[0..-1]]";

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 22, parameters, nameFormat);

        // When:
        String result = underTest.getName();

        // Then:
        assertThat(result).isEqualTo("testGetNameShouldRetrunResultOfProcess[22: 720, 720]");
    }

    @Test
    public void testInvokeExplosively() throws Throwable {
        // Given:
        final Object obj = new Object();
        final Object[] parameters = new Object[] { obj };

        Method method = getMethod("returnObjectArrayArrayMethod");

        DataProviderFrameworkMethod underTest = new DataProviderFrameworkMethod(method, 30, parameters, "%p[30]");

        // When:
        Object result = underTest.invokeExplosively(this, (Object) null);

        // Then:
        assertThat(result).isSameAs(obj);
    }

    @Test
    public void testEqualsShouldReturnTrueForSameObject() {
        // Given:
        final Object[] params = new Object[] { 1, 1.0 };

        DataProviderFrameworkMethod m = new DataProviderFrameworkMethod(method, 70, params, "%p[70]");

        // When:
        boolean result = m.equals(m);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testEqualsShouldReturnTrueForEqualObjects() {
        // Given:
        final Object[] params1 = new Object[] { "str", 3, true };
        final Object[] params2 = new Object[] { "str", 3, true };

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(method, 71, params1, "%p[71]");
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(method, 71, params2, "%p[71]");

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

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(method, 81, params1, "%p[81]");
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(method, 82, params2, "%p[82]");

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

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(method, 83, params1, "%p[83]");
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(method, 83, params2, "%p[83]");

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

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(method, 84, params1, "%p[84]");
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(method, 84, params2, "%p[84]");

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

        DataProviderFrameworkMethod m1 = new DataProviderFrameworkMethod(method, 90, params1, "%p[90]");
        DataProviderFrameworkMethod m2 = new DataProviderFrameworkMethod(method, 90, params2, "%p[90]");

        // When:
        int result = m1.hashCode();

        // Then:
        assertThat(result).isEqualTo(m2.hashCode());
    }

    // -- help methods -------------------------------------------------------------------------------------------------

    public Object returnObjectArrayArrayMethod(Object param) {
        return param;
    }
}
