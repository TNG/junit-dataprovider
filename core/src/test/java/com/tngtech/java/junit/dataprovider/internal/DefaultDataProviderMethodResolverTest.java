package com.tngtech.java.junit.dataprovider.internal;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDataProviderMethodResolverTest extends BaseTest {

    @Spy
    private DefaultDataProviderMethodResolver underTest;

    @Mock
    private DataConverter dataConverter;
    @Mock
    private TestValidator testValidator;
    @Mock
    private TestGenerator testGenerator;
    @Mock
    private TestGenerator frameworkMethodGenerator;
    @Mock
    private TestClass testClass;
    @Mock
    private FrameworkMethod testMethod;
    @Mock
    private FrameworkMethod dataProviderMethod;
    @Mock
    private UseDataProvider useDataProvider;
    @Mock
    private DataProvider dataProvider;

    @Test(expected = NullPointerException.class)
    public void testResolveShouldThrowNullPointerExceptionIfTestMethodIsNull() {
        // Given:

        // When:
        underTest.resolve(null, useDataProvider);

        // Then: expect exception
    }

    @Test(expected = NullPointerException.class)
    public void testResolveShouldThrowNullPointerExceptionIfUseDataProviderIsNull() {
        // Given:

        // When:
        underTest.resolve(testMethod, null);

        // Then: expect exception
    }

    @Test
    public void testResolveShouldReturnEmptyListForNotFoundDataProviderMethod() {
        // Given:
        final String testMethodName = "testMethodName";
        final String dataProviderMethodName = "notAvailableDataProviderMethodName";

        doReturn(testMethodName).when(testMethod).getName();

        doReturn(dataProviderMethodName).when(useDataProvider).value();
        doReturn(new Class[0]).when(useDataProvider).location();

        doReturn(asList(testClass)).when(underTest).findDataProviderLocations(testMethod, new Class[0]);
        doReturn(emptyList()).when(underTest).findDataProviderMethods(asList(testClass), testMethodName, dataProviderMethodName);

        // When:
        List<FrameworkMethod> result = underTest.resolve(testMethod, useDataProvider);

        // Then:
        assertThat(result).isEmpty();
    }

    @Test
    public void testResolveShouldReturnListContainingFoundDataProviderMethods() {
        // Given:
        final String testMethodName = "testMethodName";
        final String dataProviderMethodName = "availableDataProviderMethodName";

        doReturn(testMethodName).when(testMethod).getName();

        doReturn(dataProviderMethodName).when(useDataProvider).value();
        doReturn(new Class[0]).when(useDataProvider).location();

        doReturn(asList(testClass)).when(underTest).findDataProviderLocations(testMethod, new Class[0]);
        doReturn(asList(dataProviderMethod)).when(underTest).findDataProviderMethods(asList(testClass), testMethodName,
                dataProviderMethodName);

        // When:
        List<FrameworkMethod> result = underTest.resolve(testMethod, useDataProvider);

        // Then:
        assertThat(result).containsOnly(dataProviderMethod);
    }

    @Test
    public void testFindDataProviderLocationsShouldReturnTestClassForNotSetLocationInUseDataProviderAnnotation() {
        // Given:
        doReturn(getMethod("testFindDataProviderLocationsShouldReturnTestClassForNotSetLocationInUseDataProviderAnnotation"))
                .when(testMethod).getMethod();

        // When:
        List<TestClass> result = underTest.findDataProviderLocations(testMethod, new Class[0]);

        // Then:
        assertThatResultContainsCorrectClassesExactlyInOrder(result, this.getClass());
    }

    @Test
    public void testFindDataProviderLocationsShouldReturnTestClassContainingSetLocationInUseDataProviderAnnotation() {
        // When:
        List<TestClass> result = underTest.findDataProviderLocations(testMethod, new Class<?>[] { DataConverterTest.class });

        // Then:
        assertThatResultContainsCorrectClassesExactlyInOrder(result, DataConverterTest.class);
    }

    @Test
    public void testFindDataProviderLocationsShouldReturnTestClassesContainingSetLocationsInUseDataProviderAnnotation() {
        // Given:
        final Class<?>[] locations = new Class<?>[] { DataConverterTest.class, TestGeneratorTest.class, TestValidatorTest.class };

        // When:
        List<TestClass> result = underTest.findDataProviderLocations(testMethod, locations);

        // Then:
        assertThatResultContainsCorrectClassesExactlyInOrder(result, locations);
    }

    @Test
    public void testFindDataProviderMethodsShouldReturnEmptyListIfLocationsIsEmpty() {
        // When:
        List<FrameworkMethod> result = underTest.findDataProviderMethods(testClassesFor(), "testMethodName", "useDataProviderValue");

        // Then:
        assertThat(result).isEmpty();
    }

    @Test
    public void testFindDataProviderMethodsShouldReturnEmptyListIfFindDataProviderMethodReturnsNull() {
        // Given:
        final List<TestClass> dataProviderLocations = testClassesFor(TestGeneratorTest.class);
        final String testMethodName = "testMethodName";
        final String useDataProviderValue = "availableDataProviderMethodName";

        doReturn(null).when(underTest).findDataProviderMethod(dataProviderLocations.get(0), testMethodName, useDataProviderValue);

        // When:
        List<FrameworkMethod> result = underTest.findDataProviderMethods(dataProviderLocations, testMethodName, useDataProviderValue);

        // Then:
        assertThat(result).isEmpty();
    }

    @Test
    public void testFindDataProviderMethodsShouldReturnNotNullTestClasses() {
        // Given:
        final List<TestClass> dataProviderLocations = testClassesFor(DataConverterTest.class, TestGeneratorTest.class,
                TestValidatorTest.class);
        final String testMethodName = "testMethodName";
        final String useDataProviderValue = "availableDataProviderMethodName";

        FrameworkMethod dataProviderMethod2 = mock(FrameworkMethod.class);

        doReturn(dataProviderMethod).when(underTest).findDataProviderMethod(dataProviderLocations.get(0), testMethodName,
                useDataProviderValue);
        doReturn(null).when(underTest).findDataProviderMethod(dataProviderLocations.get(1), testMethodName, useDataProviderValue);
        doReturn(dataProviderMethod2).when(underTest).findDataProviderMethod(dataProviderLocations.get(2), testMethodName,
                useDataProviderValue);

        // When:
        List<FrameworkMethod> result = underTest.findDataProviderMethods(dataProviderLocations, testMethodName, useDataProviderValue);

        // Then:
        assertThat(result).containsExactly(dataProviderMethod, dataProviderMethod2);
    }

    @Test
    public void testFindDataProviderMethodShouldReturnNullForNotFoundDataProviderMethod() {
        // Given:
        final String testMethodName = "testMethodName";
        final String dataProviderMethodName = "availableDataProviderMethodName";

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.findDataProviderMethod(testClass, testMethodName, "notAvailableDataProviderMethodName");

        // Then:
        assertThat(result).isNull();
    }

    @Test
    public void testFindDataProviderMethodShouldReturnDataProviderMethodHavingSameNameAsTestMethodIfItExists() {
        // Given:
        final String testMethodName = "testMethodName";
        final String dataProviderMethodName = "testMethodName";

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.findDataProviderMethod(testClass, testMethodName, UseDataProvider.DEFAULT_VALUE);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testFindDataProviderMethodShouldReturnListContainingDataProviderMethodHavingDataProviderPrefixInsteadOfTestIfItExists() {
        // Given:
        final String testMethodName = "testMethodName";
        final String dataProviderMethodName = "dataProviderMethodName";

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.findDataProviderMethod(testClass, testMethodName, UseDataProvider.DEFAULT_VALUE);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testFindDataProviderMethodShouldReturnListContainingDataProviderMethodHavingDataPrefixInsteadOfTestIfItExists() {
        // Given:
        final String testMethodName = "testMethodName";
        final String dataProviderMethodName = "dataMethodName";

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.findDataProviderMethod(testClass, testMethodName, UseDataProvider.DEFAULT_VALUE);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testFindDataProviderMethodShouldReturnListContainingDataProviderMethodHavingDataProviderPrefixIfItExists() {
        // Given:
        final String testMethodName = "methodName";
        final String dataProviderMethodName = "dataProviderMethodName";

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.findDataProviderMethod(testClass, testMethodName, UseDataProvider.DEFAULT_VALUE);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testFindDataProviderMethodShouldReturnListContainingDataProviderMethodHavingDataPrefixIfItExists() {
        // Given:
        final String testMethodName = "methodName";
        final String dataProviderMethodName = "dataMethodName";

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.findDataProviderMethod(testClass, testMethodName, UseDataProvider.DEFAULT_VALUE);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testFindDataProviderMethodShouldReturnListContainingDataProviderMethodHavingExplicitelyGivenNameIfItExists() {
        // Given:
        final String testMethodName = "testMethodName";
        final String dataProviderMethodName = "availableDataProviderMethodName";

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.findDataProviderMethod(testClass, testMethodName, dataProviderMethodName);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    private List<TestClass> testClassesFor(Class<?>... classes) {
        List<TestClass> result = new ArrayList<TestClass>();
        for (Class<?> clazz : classes) {
            result.add(new TestClass(clazz));
        }
        return result;
    }

    private void assertThatResultContainsCorrectClassesExactlyInOrder(List<TestClass> result, Class<?>... expectedClasses) {
        assertThat(result).hasSameSizeAs(expectedClasses);

        for (int idx = 0; idx < expectedClasses.length; idx++) {
            assertThat(result.get(idx).getJavaClass()).isEqualTo(expectedClasses[idx]);
            assertThat(result.get(idx).getName()).isEqualTo(expectedClasses[idx].getName());
        }
    }
}
