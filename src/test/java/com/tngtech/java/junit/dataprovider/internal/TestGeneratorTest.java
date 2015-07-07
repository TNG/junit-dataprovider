package com.tngtech.java.junit.dataprovider.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;
import com.tngtech.java.junit.dataprovider.DataProvider;

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
        doReturn(anyMethod()).when(testMethod).getMethod();
        doReturn(anyMethod()).when(dataProviderMethod).getMethod();
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DLS_DEAD_LOCAL_STORE")
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
        doThrow(IllegalArgumentException.class).when(dataProviderMethod).invokeExplosively(any(), anyVararg());

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(testMethod, dataProviderMethod);

        // Then:
        assertThat(result).containsOnly(testMethod);
    }

    @Test(expected = Error.class)
    public void testGenerateExplodedTestMethodsForShouldCatchExceptionUsingDataProviderAndReThrowAsError() {
        // Given:
        doReturn(dataProvider).when(testMethod).getAnnotation(DataProvider.class);
        doThrow(IllegalArgumentException.class).when(dataConverter).convert(any(), any(Boolean.class),
                any(Class[].class), eq(dataProvider));

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(testMethod, null);

        // Then:
        assertThat(result).containsOnly(testMethod);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExplodeTestMethodsUseDataProviderShouldThrowIllegalArgumentExceptionIfDataProviderMethodThrowsException()
            throws Throwable {
        // Given:
        doThrow(NullPointerException.class).when(dataProviderMethod).invokeExplosively(null);

        // When:
        underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExplodeTestMethodsUseDataProviderShouldThrowIllegalArgumentExceptionIfDataConverterReturnsEmpty() {
        // Given:
        doReturn(new ArrayList<Object[]>()).when(dataConverter).convert(any(), any(Boolean.class), any(Class[].class),
                any(DataProvider.class));
        doReturn(dataProvider).when(dataProviderMethod).getAnnotation(DataProvider.class);

        // When:
        underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then: expect exception
    }

    @Test
    public void testExplodeTestMethodsUseDataProviderShouldReturnOneDataProviderFrameworkMethodIfDataConverterReturnsOneRow() {
        // Given:
        List<Object[]> dataConverterResult = listOfArrays(new Object[] { 1, 2, 3 });
        doReturn(dataConverterResult).when(dataConverter).convert(any(), any(Boolean.class), any(Class[].class),
                any(DataProvider.class));
        doReturn(dataProvider).when(dataProviderMethod).getAnnotation(DataProvider.class);
        doReturn("%m").when(dataProvider).format();

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then:
        assertDataProviderFrameworkMethods(result, dataConverterResult, "%m");
    }

    @Test
    public void testExplodeTestMethodsUseDataProviderShouldReturnMultipleDataProviderFrameworkMethodIfDataConverterReturnsMultipleRows() {
        // Given:
        List<Object[]> dataConverterResult = listOfArrays(new Object[] { 11, "22", 33L },
                new Object[] { 44, "55", 66L }, new Object[] { 77, "88", 99L });
        doReturn(dataConverterResult).when(dataConverter).convert(any(), any(Boolean.class), any(Class[].class),
                any(DataProvider.class));
        doReturn(dataProvider).when(dataProviderMethod).getAnnotation(DataProvider.class);
        doReturn("%c").when(dataProvider).format();

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then:
        assertDataProviderFrameworkMethods(result, dataConverterResult, "%c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExplodeTestMethodsDataProviderShouldIllegalArgumentExceptionIfDataConverterReturnsAnEmptyList() {
        // Given:
        doReturn(new ArrayList<Object[]>()).when(dataConverter).convert(any(), any(Boolean.class), any(Class[].class),
                any(DataProvider.class));

        // When:
        underTest.explodeTestMethod(testMethod, dataProvider);

        // Then: expect exception
    }

    @Test
    public void testExplodeTestMethodsDataProviderShouldReturnOneDataProviderFrameworkMethodIfDataConverterReturnsOneRow() {
        // Given:
        List<Object[]> dataConverterResult = listOfArrays(new Object[] { 1, "test1" });
        doReturn(dataConverterResult).when(dataConverter).convert(any(), any(Boolean.class), any(Class[].class),
                any(DataProvider.class));
        doReturn("%i").when(dataProvider).format();

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
        doReturn(dataConverterResult).when(dataConverter).convert(any(), any(Boolean.class), any(Class[].class),
                any(DataProvider.class));
        doReturn("%p[0]").when(dataProvider).format();

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProvider);

        // Then:
        assertDataProviderFrameworkMethods(result, dataConverterResult, "%p[0]");
    }
}
