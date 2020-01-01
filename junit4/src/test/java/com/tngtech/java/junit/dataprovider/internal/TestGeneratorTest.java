package com.tngtech.java.junit.dataprovider.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;
import com.tngtech.java.junit.dataprovider.DataProvider;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@RunWith(MockitoJUnitRunner.class)
public class TestGeneratorTest extends BaseTest {

    @InjectMocks
    private TestGenerator underTest;

    @Mock
    private DataConverter dataConverter;
    @Mock
    private FrameworkMethod testMethod;
    @Mock
    private FrameworkMethod dataProviderMethod;
    @Mock
    private DataProvider dataProvider;

    @Before
    public void setup() {
        TestGenerator.dataProviderDataCache.clear();

        when(testMethod.getMethod()).thenReturn(anyMethod());
        when(dataProviderMethod.getMethod()).thenReturn(anyMethod());
    }

    @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = NullPointerException.class)
    public void testTestGeneratorShouldThrowNullPointerExceptionIfDataConverterIsNull() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        TestGenerator result = new TestGenerator(null);

        // Then: expect exception
    }

    @Test
    public void testGenerateExplodedTestMethodsForShouldReturnEmptyListIfArgumentIsNull() {
        // Given:

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(null, null);

        // Then:
        assertThat(result).isEmpty();
    }

    @Test
    public void testGenerateExplodedTestMethodsForShouldReturnOriginalTestMethodIfNoDataProviderIsUsed() {
        // Given:

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(testMethod, null);

        // Then:
        assertThat(result).containsOnly(testMethod);
    }

    @Test(expected = Error.class)
    public void testGenerateExplodedTestMethodsForShouldCatchExceptionUsingUseDataProviderAndReThrowAsError()
            throws Throwable {
        // Given:
        when(dataProviderMethod.getAnnotation(DataProvider.class)).thenReturn(dataProvider);
        when(dataProviderMethod.invokeExplosively(any(), any())).thenThrow(IllegalArgumentException.class);

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(testMethod, dataProviderMethod);

        // Then:
        assertThat(result).containsOnly(testMethod);
    }

    @Test(expected = Error.class)
    public void testGenerateExplodedTestMethodsForShouldCatchExceptionUsingDataProviderAndReThrowAsError() {
        // Given:
        when(testMethod.getAnnotation(DataProvider.class)).thenReturn(dataProvider);
        when(dataConverter.convert(any(), any(Boolean.class), any(Class[].class), eq(dataProvider)))
                .thenThrow(IllegalArgumentException.class);

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(testMethod, null);

        // Then:
        assertThat(result).containsOnly(testMethod);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExplodeTestMethodsUseDataProviderShouldThrowIllegalArgumentExceptionIfDataProviderMethodThrowsException()
            throws Throwable {
        // Given:
        when(dataProviderMethod.getAnnotation(DataProvider.class)).thenReturn(dataProvider);
        when(dataProviderMethod.invokeExplosively(null)).thenThrow(NullPointerException.class);

        // When:
        underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExplodeTestMethodsUseDataProviderShouldThrowIllegalArgumentExceptionIfDataConverterReturnsEmpty() {
        // Given:
        when(dataConverter.convert(any(), any(Boolean.class), any(Class[].class), any(DataProvider.class)))
                .thenReturn(new ArrayList<Object[]>());
        when(dataProviderMethod.getAnnotation(DataProvider.class)).thenReturn(dataProvider);

        // When:
        underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then: expect exception
    }

    @Test
    public void testExplodeTestMethodsUseDataProviderShouldReturnOneDataProviderFrameworkMethodIfDataConverterReturnsOneRow() throws Throwable {
        // Given:
        List<Object[]> dataConverterResult = listOfArrays(new Object[] { 1, 2, 3 });
        when(dataConverter.convert(any(), any(Boolean.class), any(Class[].class), any(DataProvider.class))).thenReturn(dataConverterResult);
        when(dataProviderMethod.getAnnotation(DataProvider.class)).thenReturn(dataProvider);
        when(dataProvider.format()).thenReturn("%m");

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then:
        assertDataProviderFrameworkMethods(result, dataConverterResult, "%m");
        verify(dataProviderMethod).invokeExplosively(null);
    }

    @Test
    public void testExplodeTestMethodsUseDataProviderShouldUseCachedDataProviderResultIfAvailable() {
        // Given:
        Object data = new Object[][] { { 1 } };
        TestGenerator.dataProviderDataCache.put(dataProviderMethod, data);

        when(dataConverter.convert(any(), any(Boolean.class), any(Class[].class), any(DataProvider.class)))
                .thenReturn(listOfArrays(new Object[] { 1 }));
        when(dataProviderMethod.getAnnotation(DataProvider.class)).thenReturn(dataProvider);
        when(dataProvider.format()).thenReturn("%m");

        // When:
        underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then:
        verify(dataProviderMethod).getAnnotation(DataProvider.class);
        verify(dataConverter).convert(eq(data), eq(false), any(Class[].class), eq(dataProvider));
        verifyNoMoreInteractions(dataProviderMethod, dataConverter);
    }

    @Test
    public void testExplodeTestMethodsUseDataProviderShouldReturnMultipleDataProviderFrameworkMethodIfDataConverterReturnsMultipleRows()
            throws Throwable {
        // Given:
        List<Object[]> dataConverterResult = listOfArrays(new Object[] { 11, "22", 33L }, new Object[] { 44, "55", 66L },
                new Object[] { 77, "88", 99L });
        when(dataConverter.convert(any(), any(Boolean.class), any(Class[].class), any(DataProvider.class))).thenReturn(dataConverterResult);
        when(dataProviderMethod.getAnnotation(DataProvider.class)).thenReturn(dataProvider);
        when(dataProvider.format()).thenReturn("%c");
        when(dataProvider.cache()).thenReturn(false);

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then:
        assertThat(TestGenerator.dataProviderDataCache).isEmpty();
        assertDataProviderFrameworkMethods(result, dataConverterResult, "%c");
        verify(dataProviderMethod).invokeExplosively(null);
    }

    @Test
    public void testExplodeTestMethodsUseDataProviderShouldReturnFrameworkMethodInjectedToUseDataProviderMethodIfExists() throws Throwable {
        // Given:
        final Method method = getMethod("dataProviderMethod");
        when(dataProviderMethod.getMethod()).thenReturn(method);

        List<Object[]> dataConverterResult = listOfArrays(new Object[] { null });
        when(dataConverter.convert(any(), anyBoolean(), any(Class[].class), any(DataProvider.class))).thenReturn(dataConverterResult);
        when(dataProviderMethod.getAnnotation(DataProvider.class)).thenReturn(dataProvider);
        when(dataProvider.format()).thenReturn(DataProvider.DEFAULT_FORMAT);
        when(dataProvider.cache()).thenReturn(true);

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then:
        assertThat(TestGenerator.dataProviderDataCache).hasSize(1).containsKey(dataProviderMethod);
        assertThat(result).hasSize(1);
        verify(dataProviderMethod).invokeExplosively(null, testMethod);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExplodeTestMethodsDataProviderShouldIllegalArgumentExceptionIfDataConverterReturnsAnEmptyList() {
        // Given:
        when(dataConverter.convert(any(), any(Boolean.class), any(Class[].class), any(DataProvider.class))).thenReturn(new ArrayList<Object[]>());

        // When:
        underTest.explodeTestMethod(testMethod, dataProvider);

        // Then: expect exception
    }

    @Test
    public void testExplodeTestMethodsDataProviderShouldReturnOneDataProviderFrameworkMethodIfDataConverterReturnsOneRow() {
        // Given:
        List<Object[]> dataConverterResult = listOfArrays(new Object[] { 1, "test1" });
        when(dataConverter.convert(any(), any(Boolean.class), any(Class[].class), any(DataProvider.class))).thenReturn(dataConverterResult);
        when(dataProvider.format()).thenReturn("%i");

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProvider);

        // Then:
        assertDataProviderFrameworkMethods(result, dataConverterResult, "%i");
    }

    @Test
    public void testExplodeTestMethodsDataProviderShouldReturnMultipleDataProviderFrameworkMethodIfDataProviderValueArrayReturnsMultipleRows() {
        // Given:
        List<Object[]> dataConverterResult = listOfArrays(new Object[] { "2a", "foo" }, new Object[] { "3b", "bar" },
                new Object[] { "4c", "baz" });
        when(dataConverter.convert(any(), any(Boolean.class), any(Class[].class), any(DataProvider.class))).thenReturn(dataConverterResult);
        when(dataProvider.format()).thenReturn("%p[0]");

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProvider);

        // Then:
        assertDataProviderFrameworkMethods(result, dataConverterResult, "%p[0]");
    }

    // -- helper methods to find non-mockable Method objects (due to final :-( ) ---------------------------------------
    public static Object[][] dataProviderMethod(FrameworkMethod method) {
        return new Object[][] { { method } };
    }
}
