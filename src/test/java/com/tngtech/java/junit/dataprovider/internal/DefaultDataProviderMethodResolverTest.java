package com.tngtech.java.junit.dataprovider.internal;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunnerTest;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.java.junit.dataprovider.internal.DataConverter;
import com.tngtech.java.junit.dataprovider.internal.DefaultDataProviderMethodResolver;
import com.tngtech.java.junit.dataprovider.internal.TestGenerator;
import com.tngtech.java.junit.dataprovider.internal.TestValidator;

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

    @Before
    public void setup() {
        doReturn(UseDataProvider.DEFAULT_VALUE).when(useDataProvider).value();
        doReturn(new Class[0]).when(useDataProvider).location();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolveShouldThrowIllegalArgumentExceptionIfTestMethodIsNull() {
        // Given:

        // When:
        underTest.resolve(null, useDataProvider);

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolveShouldThrowIllegalArgumentExceptionIfUseDataProviderIsNull() {
        // Given:

        // When:
        underTest.resolve(testMethod, null);

        // Then: expect exception
    }

    @Test
    public void testResolveShouldReturnNullForNotFoundDataProviderMethod() {
        // Given:
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn("notAvailableDataProviderMethod").when(useDataProvider).value();

        doReturn(testClass).when(underTest).findDataProviderLocation(testMethod, useDataProvider);
        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn("availableDataProviderMethod").when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.resolve(testMethod, useDataProvider);

        // Then:
        assertThat(result).isNull();
    }

    @Test
    public void testResolveShouldReturnDataProviderMethodWithSameNameAsTestIfItExists() {
        // Given:
        final String testMethodName = "testMethodName";

        doReturn(testMethodName).when(testMethod).getName();

        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(testClass).when(underTest).findDataProviderLocation(testMethod, useDataProvider);

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(testMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.resolve(testMethod, useDataProvider);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testResolveShouldReturnDataProviderMethodWithDataProviderPrefixInsteadOfTestIfItExists() {
        // Given:
        final String testMethodName = "testMethodName";
        final String dataProviderMethodName = "dataProviderMethodName";

        doReturn(testMethodName).when(testMethod).getName();

        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);

        doReturn(testClass).when(underTest).findDataProviderLocation(testMethod, useDataProvider);

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.resolve(testMethod, useDataProvider);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testResolveShouldReturnDataProviderMethodWithDataPrefixInsteadOfTestIfItExists() {
        // Given:
        final String testMethodName = "testMethodName";
        final String dataProviderMethodName = "dataMethodName";

        doReturn(testMethodName).when(testMethod).getName();

        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(testClass).when(underTest).findDataProviderLocation(testMethod, useDataProvider);

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.resolve(testMethod, useDataProvider);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testResolveShouldReturnDataProviderMethodWithDataProviderPrefixIfItExists() {
        // Given:
        final String testMethodName = "methodName";
        final String dataProviderMethodName = "dataProviderMethodName";

        doReturn(testMethodName).when(testMethod).getName();

        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);

        doReturn(testClass).when(underTest).findDataProviderLocation(testMethod, useDataProvider);

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.resolve(testMethod, useDataProvider);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testResolveShouldReturnDataProviderMethodWithDataPrefixIfItExists() {
        // Given:
        final String testMethodName = "methodName";
        final String dataProviderMethodName = "dataMethodName";

        doReturn(testMethodName).when(testMethod).getName();

        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(testClass).when(underTest).findDataProviderLocation(testMethod, useDataProvider);

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.resolve(testMethod, useDataProvider);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testResolveShouldReturnDataProviderMethodWithExplicitelyGivenNameIfItExists() {
        // Given:
        final String dataProviderMethodName = "availableDataProviderMethod";

        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(dataProviderMethodName).when(useDataProvider).value();

        doReturn(testClass).when(underTest).findDataProviderLocation(testMethod, useDataProvider);

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.resolve(testMethod, useDataProvider);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testFindDataProviderLocationShouldReturnTestClassForNotSetLocationInUseDataProviderAnnotation() {
        // Given:
        doReturn(new Class<?>[0]).when(useDataProvider).location();
        doReturn(getMethod("testFindDataProviderLocationShouldReturnTestClassForNotSetLocationInUseDataProviderAnnotation"))
                .when(testMethod).getMethod();

        // When:
        TestClass result = underTest.findDataProviderLocation(testMethod, useDataProvider);

        // Then:
        assertThat(result.getJavaClass()).isEqualTo(DefaultDataProviderMethodResolverTest.class);
    }

    @Test
    public void testFindDataProviderLocationShouldReturnTestClassContainingSetLocationInUseDataProviderAnnotation() {
        // Given:
        final Class<?> dataProviderLocation = DataProviderRunnerTest.class;

        doReturn(new Class<?>[] { dataProviderLocation }).when(useDataProvider).location();

        // When:
        TestClass result = underTest.findDataProviderLocation(testMethod, useDataProvider);

        // Then:
        assertThat(result).isNotNull();
        // assertThat(result.getJavaClass()).isEqualTo(dataProviderLocation);
        assertThat(result.getName()).isEqualTo(dataProviderLocation.getName());
    }
}
