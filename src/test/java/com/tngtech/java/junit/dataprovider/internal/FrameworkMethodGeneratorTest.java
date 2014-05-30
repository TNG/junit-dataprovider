package com.tngtech.java.junit.dataprovider.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
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
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(MockitoJUnitRunner.class)
public class FrameworkMethodGeneratorTest extends BaseTest {

    @InjectMocks
    private FrameworkMethodGenerator underTest;

    @Mock
    private DataConverter dataConverter;
    @Mock
    private FrameworkMethod testMethod;
    @Mock
    private FrameworkMethod dataProviderMethod;
    @Mock
    private UseDataProvider useDataProvider;
    @Mock
    private DataProvider dataProvider;

    @Before
    public void setup() {
        doReturn(anyMethod()).when(testMethod).getMethod();
        doReturn(anyMethod()).when(dataProviderMethod).getMethod();
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = NullPointerException.class)
    public void testFrameworkMethodGenerator() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        FrameworkMethodGenerator result = new FrameworkMethodGenerator(null);

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

    // TODO
    // @Test
    // public void testGenerateExplodedTestMethodsForShouldReturnExplodedTestMethodsForValidGivenDataProvider() {
    // // Given:
    // List<FrameworkMethod> explodedMethods = new ArrayList<FrameworkMethod>();
    // explodedMethods.add(mock(FrameworkMethod.class));
    // explodedMethods.add(mock(FrameworkMethod.class));
    // // doReturn(explodedMethods).when(underTest).explodeTestMethod(testMethod, dataProviderMethod);
    //
    // // When:
    // underTest.generateExplodedTestMethodsFor(asList(testMethod));
    //
    // // Then:
    // verify(frameworkMethodGenerator).generateExplodedTestMethodsFor(testMethod, dataProviderMethod);
    // // assertThat(result).hasSize(2).containsAll(explodedMethods);
    // }

    @Test(expected = Error.class)
    public void testExplodeTestMethodsUseDataProviderShouldThrowErrorIfDataProviderMethodThrowsException()
            throws Throwable {
        // Given:
        doThrow(NullPointerException.class).when(dataProviderMethod).invokeExplosively(null);

        // When:
        underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then: expect exception
    }

    @Test(expected = Error.class)
    public void testExplodeTestMethodsUseDataProviderShouldThrowErrorIfDataConverterReturnsEmpty() {
        // Given:
        doReturn(new ArrayList<Object[]>()).when(dataConverter).convert(any(), any(Class[].class));

        // When:
        underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then: expect exception
    }

    @Test
    public void testExplodeTestMethodsUseDataProviderShouldReturnOneDataProviderFrameworkMethodIfDataConverterReturnsOneRow() {
        // Given:
        List<Object[]> dataConverterResult = listOfArrays(new Object[] { 1, 2, 3 });
        doReturn(dataConverterResult).when(dataConverter).convert(any(), any(Class[].class));

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then:
        assertDataProviderFrameworkMethods(result, dataConverterResult);
    }

    @Test
    public void testExplodeTestMethodsUseDataProviderShouldReturnMultipleDataProviderFrameworkMethodIfDataConverterReturnsMultipleRows() {
        // Given:
        List<Object[]> dataConverterResult = listOfArrays(new Object[] { 11, "22", 33L },
                new Object[] { 44, "55", 66L }, new Object[] { 77, "88", 99L });
        doReturn(dataConverterResult).when(dataConverter).convert(any(), any(Class[].class));

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then:
        assertDataProviderFrameworkMethods(result, dataConverterResult);
    }

    @Test(expected = Error.class)
    public void testExplodeTestMethodsDataProviderShouldThrowErrorIfDataConverterReturnsAnEmptyList() {
        // Given:
        doReturn(new ArrayList<Object[]>()).when(dataConverter).convert(any(), any(Class[].class));

        // When:
        underTest.explodeTestMethod(testMethod, dataProvider);

        // Then: expect exception
    }

    @Test
    public void testExplodeTestMethodsDataProviderShouldReturnOneDataProviderFrameworkMethodIfDataConverterReturnsOneRow() {
        // Given:
        List<Object[]> dataConverterResult = listOfArrays(new Object[] { 1, "test1" });
        doReturn(dataConverterResult).when(dataConverter).convert(any(), any(Class[].class));

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProvider);

        // Then:
        assertDataProviderFrameworkMethods(result, dataConverterResult);
    }

    @Test
    public void testExplodeTestMethodsDataProviderShouldReturnMultipleDataProviderFrameworkMethodIfDataProviderValueArrayReturnsMultipleRows() {
        // Given:
        List<Object[]> dataConverterResult = listOfArrays(new Object[] { "2a", "foo" }, new Object[] { "3b", "bar" },
                new Object[] { "4c", "baz" });
        doReturn(dataConverterResult).when(dataConverter).convert(any(), any(Class[].class));

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProvider);

        // Then:
        assertDataProviderFrameworkMethods(result, dataConverterResult);
    }
}
