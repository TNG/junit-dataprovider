package com.tngtech.java.junit.dataprovider;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.tngtech.java.junit.dataprovider.internal.DataConverter;
import com.tngtech.java.junit.dataprovider.internal.DefaultDataProviderMethodResolver;
import com.tngtech.java.junit.dataprovider.internal.TestGenerator;
import com.tngtech.java.junit.dataprovider.internal.TestValidator;

public class DataProviderRunnerTest extends BaseTest {

    // for testing exceptions in @BeforeClass
    private static volatile Throwable classSetupException = null;

    @Spy
    private DataProviderRunner underTest;

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
    private DataProviderMethodResolver dataProviderMethodResolver;
    @Mock
    private FrameworkMethod testMethod;
    @Mock
    private FrameworkMethod dataProviderMethod;
    @Mock
    private UseDataProvider useDataProvider;
    @Mock
    private DataProvider dataProvider;

    @BeforeClass
    public static void classSetup() throws Throwable {
        if (classSetupException != null) {
            throw classSetupException;
        }
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    @Before
    public void setup() throws Exception {
        classSetupException = null;

        underTest = new DataProviderRunner(DataProviderRunnerTest.class);

        MockitoAnnotations.initMocks(this);
        underTest.dataConverter = dataConverter; // override default dataConverter
        underTest.testValidator = testValidator;
        underTest.testGenerator = testGenerator;
        underTest.testGenerator = frameworkMethodGenerator;

        doReturn(testClass).when(underTest).getTestClassInt();

        doReturn(anyMethod()).when(testMethod).getMethod();
        doReturn("testMethod").when(testMethod).getName();

        doReturn(anyMethod()).when(dataProviderMethod).getMethod();

        doReturn(UseDataProvider.DEFAULT_VALUE).when(useDataProvider).value();
        doReturn(new Class<?>[] { DataProviderMethodResolver.class }).when(useDataProvider).resolver();
    }

    @Test
    public void testDataProviderRunner() throws Exception {
        // Given:
        @SuppressWarnings("rawtypes")
        Class clazz = DataProviderRunnerTest.class;

        // When:
        DataProviderRunner underTest = new DataProviderRunner(clazz);

        // Then:
        assertThat(underTest).isNotNull();
        assertThat(underTest.getTestClass()).isNotNull();
        assertThat(underTest.getTestClass().getJavaClass()).isEqualTo(clazz);
    }

    @Test
    public void testInitializeHelpers() throws Exception {
        // Given:
        final DataConverter newDataConverter = new DataConverter();
        final TestGenerator newTestGenerator = new TestGenerator(this.dataConverter);
        final TestValidator newTestValidator = new TestValidator(this.dataConverter);

        underTest = new DataProviderRunner(DataProviderRunnerTest.class) {
            @Override
            protected void initializeHelpers() {
                super.initializeHelpers();
                this.dataConverter = newDataConverter;
                this.testGenerator = newTestGenerator;
                this.testValidator = newTestValidator;
            }
        };

        // When:
        underTest.initializeHelpers();

        // Then:
        assertThat(underTest.dataConverter).isSameAs(newDataConverter);
        assertThat(underTest.testGenerator).isSameAs(newTestGenerator);
        assertThat(underTest.testValidator).isSameAs(newTestValidator);
    }

    @Test(expected = NullPointerException.class)
    public void testValidateInstanceMethodsShouldThrowNullPointerExceptionIfErrorsIsNull() {
        // Given:

        // When:
        underTest.validateInstanceMethods(null);

        // Then: expect exception
    }

    @Test
    public void testValidateInstanceMethodsShouldAddExceptionIfComputeTestMethodsReturnsNoTestMethods() {
        // Given:
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(asList()).when(underTest).computeTestMethods();

        // When:
        underTest.validateInstanceMethods(errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).hasMessage("No runnable methods");
    }

    @Test
    public void testValidateInstanceMethodsShouldNotThrowExceptionIfComputeTestMethodsWouldThrowExceptionButErrorsAlreadyExistsBefore() {
        // Given:
        List<Throwable> errors = new ArrayList<Throwable>();

        doAnswer(new Answer<Void>() {
            @SuppressWarnings("unchecked")
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((List<Throwable>) invocation.getArguments()[0]).add(new Error());
                return null;
            }
        }).when(underTest).validateTestMethods(errors);

        doThrow(IllegalArgumentException.class).when(underTest).computeTestMethods();

        // When:
        underTest.validateInstanceMethods(errors);

        // Then: no exception
    }

    @Test
    public void testValidateInstanceMethodsShouldNotThrowExceptionIfNoErrorsExistAndTestMethodsAreAvailable() {
        // Given:
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(asList(testMethod)).when(underTest).computeTestMethods();

        // When:
        underTest.validateInstanceMethods(errors);

        // Then:
        assertThat(errors).isEmpty();
    }

    @Test(expected = NullPointerException.class)
    public void testValidateTestMethodsShouldThrowNullPointerExceptionIfArgumentIsNull() {
        // Given:

        // When:
        underTest.validateTestMethods(null);

        // Then: expect exception
    }

    @Test
    public void testValidateTestMethodsShouldCallTestValidatorValidateTestMethodForSingleTestMethod() {
        // Given:
        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(Test.class);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        verify(testValidator).validateTestMethod(testMethod, errors);
        verifyNoMoreInteractions(testValidator);
    }

    @Test
    public void testValidateTestMethodsShouldCallTestValidatorValidateTestMethodForMultipleTestMethods() {
        // Given:
        FrameworkMethod testMethod2 = mock(FrameworkMethod.class);
        FrameworkMethod testMethod3 = mock(FrameworkMethod.class);
        doReturn(asList(testMethod, testMethod2, testMethod3)).when(testClass).getAnnotatedMethods(Test.class);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        verify(testValidator).validateTestMethod(testMethod, errors);
        verify(testValidator).validateTestMethod(testMethod2, errors);
        verify(testValidator).validateTestMethod(testMethod3, errors);
        verifyNoMoreInteractions(testValidator);
    }

    @Test
    public void testValidateTestMethodsShouldAddErrorIfDataProviderMethodNotFoundForMethodWithUseDataProviderUsingDefaultResolver() {
        // Given:
        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(UseDataProvider.class);
        doReturn(null).when(underTest).getDataProviderMethod(testMethod);
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(new Class[] { DefaultDataProviderMethodResolver.class }).when(useDataProvider).resolver();

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage())
                .contains("No valid dataprovider found for test 'testMethod' using the default resolver. By convention")
                .contains("or is explicitely set");

        verifyZeroInteractions(testValidator);
    }

    @Test
    public void testValidateTestMethodsShouldAddErrorIfDataProviderMethodNotFoundForMethodWithUseDataProviderUsingCustomResolver() {
        // Given:
        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(UseDataProvider.class);
        doReturn(null).when(underTest).getDataProviderMethod(testMethod);
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(new Class[] { null, DefaultDataProviderMethodResolver.class }).when(useDataProvider).resolver();

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains("No valid dataprovider found for test 'testMethod' using custom resolvers: ")
                .containsIgnoringCase("[null, " + DefaultDataProviderMethodResolver.class + "]. Please examine");
        verifyZeroInteractions(testValidator);
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateTestMethodsShouldThrowIllegalStateExceptionIfDataProviderAnnotationNotFoundOnDataProviderMethod() {
        // Given:
        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(UseDataProvider.class);
        doReturn(dataProviderMethod).when(underTest).getDataProviderMethod(testMethod);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then: expect exception
    }

    @Test
    public void testValidateTestMethodsShouldCallTestValidatorValidateDataProviderMethodIfDataProviderMethodFound() {
        // Given:
        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(UseDataProvider.class);
        doReturn(dataProviderMethod).when(underTest).getDataProviderMethod(testMethod);
        doReturn(dataProvider).when(dataProviderMethod).getAnnotation(DataProvider.class);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        verify(testValidator).validateDataProviderMethod(dataProviderMethod, dataProvider, errors);
        verifyNoMoreInteractions(testValidator);
    }

    @Test
    public void testValidateTestMethodsShouldWorkCorrectlyForMultipleMethodsAnnotatedWithUseDataProvider() {
        // Given:
        String dataProviderName = "notFoundDataProvider3";

        FrameworkMethod testMethod2 = mock(FrameworkMethod.class);
        FrameworkMethod testMethod3 = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod2 = mock(FrameworkMethod.class);
        DataProvider dataProvider2 = mock(DataProvider.class);

        doReturn(asList(testMethod, testMethod2, testMethod3)).when(testClass).getAnnotatedMethods(
                UseDataProvider.class);
        doReturn(dataProviderMethod).when(underTest).getDataProviderMethod(testMethod);
        doReturn(dataProvider).when(dataProviderMethod).getAnnotation(DataProvider.class);
        doReturn(dataProviderMethod2).when(underTest).getDataProviderMethod(testMethod2);
        doReturn(dataProvider2).when(dataProviderMethod2).getAnnotation(DataProvider.class);
        doReturn(null).when(underTest).getDataProviderMethod(testMethod3);

        doReturn(useDataProvider).when(testMethod3).getAnnotation(UseDataProvider.class);
        doReturn(dataProviderName).when(useDataProvider).value();

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).containsIgnoringCase("no valid dataprovider found for test");

        verify(testValidator).validateDataProviderMethod(dataProviderMethod, dataProvider, errors);
        verify(testValidator).validateDataProviderMethod(dataProviderMethod2, dataProvider2, errors);
        verifyNoMoreInteractions(testMethod);
    }

    @Test
    public void testComputeTestMethodsShouldCallGenerateExplodedTestMethodsAndCacheResultIfCalledTheFirstTime() {
        // Given:
        underTest.computedTestMethods = null;
        doReturn(new ArrayList<FrameworkMethod>()).when(underTest).generateExplodedTestMethodsFor(
                anyListOf(FrameworkMethod.class));

        // When:
        List<FrameworkMethod> result = underTest.computeTestMethods();

        // Then:
        assertThat(result).isEqualTo(underTest.computedTestMethods);

        InOrder inOrder = inOrder(underTest);
        inOrder.verify(underTest).computeTestMethods();
        inOrder.verify(underTest).generateExplodedTestMethodsFor(anyListOf(FrameworkMethod.class));
        verifyNoMoreInteractions(underTest);
    }

    @Test
    public void testComputeTestMethodsShouldNotCallGenerateExplodedTestMethodsAndUseCachedResultIfCalledTheSecondTime() {
        // Given:
        final List<FrameworkMethod> expected = new ArrayList<FrameworkMethod>();

        underTest.computedTestMethods = expected;

        doReturn(expected).when(underTest).generateExplodedTestMethodsFor(anyListOf(FrameworkMethod.class));

        // When:
        List<FrameworkMethod> result = underTest.computeTestMethods();

        // Then:
        assertThat(result).isSameAs(expected);
        assertThat(underTest.computedTestMethods).isSameAs(expected);

        verify(underTest).computeTestMethods();
        verifyNoMoreInteractions(underTest);
    }

    @Test(expected = NullPointerException.class)
    public void testFilterShouldThrowNullPointerExceptionForNull() throws Exception {
        // Given:

        // When:
        underTest.filter(null);

        // Then: expect exception
    }

    @Test
    public void testFilterShould() throws Exception {
        // Given:
        Filter filter = Filter.ALL;

        // When:
        underTest.filter(filter);

        // Then:
        assertThat(underTest.getDescription().getChildren().size()).isGreaterThan(0);
    }

    @Test
    public void testGenerateExplodedTestMethodsForShouldReturnEmptyListIfArgumentIsNull() {
        // Given:

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(null);

        // Then:
        assertThat(result).isEmpty();
    }

    @Test
    public void testGenerateExplodedTestMethodsForShouldReturnEmptyListIfArgumentIsEmptyList() {
        // Given:
        List<FrameworkMethod> testMethods = new ArrayList<FrameworkMethod>();

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(testMethods);

        // Then:
        assertThat(result).isEmpty();
    }

    @Test
    public void testGenerateExplodedTestMethodsForShouldCallFrameworkMethodGeneratorWithNotFoundDataProviderMethodAndAddResult() {
        // Given:
        doReturn(null).when(underTest).getDataProviderMethod(testMethod);
        doReturn(asList(testMethod)).when(frameworkMethodGenerator).generateExplodedTestMethodsFor(testMethod, null);

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(asList(testMethod));

        // Then:
        assertThat(result).containsOnly(testMethod);

        verify(frameworkMethodGenerator).generateExplodedTestMethodsFor(testMethod, null);
        verifyNoMoreInteractions(frameworkMethodGenerator);
    }

    @Test
    public void testGenerateExplodedTestMethodsForShouldCallFrameworkMethodGeneratorWithFoundDataProviderMethodAndAddResult() {
        // Given:
        doReturn(dataProviderMethod).when(underTest).getDataProviderMethod(testMethod);

        List<FrameworkMethod> explodedMethods = new ArrayList<FrameworkMethod>();
        explodedMethods.add(mock(FrameworkMethod.class));
        explodedMethods.add(mock(FrameworkMethod.class));
        doReturn(explodedMethods).when(frameworkMethodGenerator).generateExplodedTestMethodsFor(testMethod,
                dataProviderMethod);

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(asList(testMethod));

        // Then:
        assertThat(result).hasSize(2).containsAll(explodedMethods);

        verify(frameworkMethodGenerator).generateExplodedTestMethodsFor(testMethod, dataProviderMethod);
        verifyNoMoreInteractions(frameworkMethodGenerator);
    }

    @Test
    public void testGenerateExplodedTestMethodsForShouldCallFrameworkMethodGeneratorForAllTestMethodsAndAddResult() {
        // Given:
        FrameworkMethod testMethod2 = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod2 = mock(FrameworkMethod.class);

        doReturn(dataProviderMethod).when(underTest).getDataProviderMethod(testMethod);
        doReturn(dataProviderMethod2).when(underTest).getDataProviderMethod(testMethod2);

        List<FrameworkMethod> explodedMethods = new ArrayList<FrameworkMethod>();
        explodedMethods.add(mock(FrameworkMethod.class));
        explodedMethods.add(mock(FrameworkMethod.class));
        doReturn(explodedMethods).when(frameworkMethodGenerator).generateExplodedTestMethodsFor(testMethod,
                dataProviderMethod);

        List<FrameworkMethod> explodedMethods2 = new ArrayList<FrameworkMethod>();
        explodedMethods2.add(mock(FrameworkMethod.class));
        explodedMethods2.add(mock(FrameworkMethod.class));
        explodedMethods2.add(mock(FrameworkMethod.class));
        doReturn(explodedMethods2).when(frameworkMethodGenerator).generateExplodedTestMethodsFor(testMethod2,
                dataProviderMethod2);

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(asList(testMethod, testMethod2));

        // Then:
        assertThat(result).hasSize(5).containsAll(explodedMethods).containsAll(explodedMethods2);

        verify(frameworkMethodGenerator).generateExplodedTestMethodsFor(testMethod, dataProviderMethod);
        verify(frameworkMethodGenerator).generateExplodedTestMethodsFor(testMethod2, dataProviderMethod2);
        verifyNoMoreInteractions(frameworkMethodGenerator);
    }

    @Test
    public void testGetDataProviderMethodShouldReturnNullForNonDataProviderMethod() {
        // Given:
        doReturn(null).when(testMethod).getAnnotation(UseDataProvider.class);

        // When:
        FrameworkMethod result = underTest.getDataProviderMethod(testMethod);

        // Then:
        assertThat(result).isNull();
    }

    @Test
    public void testGetDataProviderMethodShouldReturnNullForNotFoundDataProviderMethod() {
        // Given:
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn("notAvailableDataProviderMethod").when(useDataProvider).value();

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn("availableDataProviderMethod").when(dataProviderMethod).getName();

        doReturn(dataProviderMethodResolver).when(underTest).getResolverInstanceInt(any(Class.class));

        // When:
        FrameworkMethod result = underTest.getDataProviderMethod(testMethod);

        // Then:
        assertThat(result).isNull();
    }

    public void testGetDataProviderMethodShouldReturnNullIfTestMethodHasNoUseDataProviderAnnotation() throws Exception {
        // Given:
        doReturn(null).when(testMethod).getAnnotation(UseDataProvider.class);

        // When:
        FrameworkMethod result = underTest.getDataProviderMethod(testMethod);

        // Then:
        assertThat(result).isNull();
    }

    @Test
    public void testGetDataProviderMethodShouldReturnNullIfUseDataProviderResolversAreEmpty() throws Exception {
        // Given:
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);

        doReturn(dataProviderMethodResolver).when(underTest).getResolverInstanceInt(any(Class.class));

        // When:
        FrameworkMethod result = underTest.getDataProviderMethod(testMethod);

        // Then:
        assertThat(result).isNull();
    }

    @Test
    public void testGetDataProviderMethodShouldReturnFirstNonNullResultDataProviderMethodIfMultipleResolvers() {
        // Given:
        final DataProviderMethodResolver resolver2 = mock(DataProviderMethodResolver.class);
        final DataProviderMethodResolver resolver3 = mock(DataProviderMethodResolver.class);

        final FrameworkMethod expected1 = mock(FrameworkMethod.class);
        final FrameworkMethod expected2 = mock(FrameworkMethod.class);

        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(new Class[] { null, null, null }).when(useDataProvider).resolver();
        doReturn(dataProviderMethodResolver).doReturn(resolver2).doReturn(resolver3).when(underTest).getResolverInstanceInt(any(Class.class));

        doReturn(null).when(dataProviderMethodResolver).resolve(testMethod, useDataProvider);
        doReturn(expected1).when(resolver2).resolve(testMethod, useDataProvider);
        doReturn(expected2).when(resolver3).resolve(testMethod, useDataProvider);

        // When:
        FrameworkMethod result = underTest.getDataProviderMethod(testMethod);

        // Then:
        assertThat(result).isSameAs(expected1);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetResolverInstanceIntShouldThrowIllegalStateExceptionIfNoDefaultConstructorExists() {
        // When:
        underTest.getResolverInstanceInt(NoDefaultConstructor.class);

        // Then: expect exception
    }

    @Test(expected = IllegalStateException.class)
    public void testGetResolverInstanceIntShouldThrowIllegalStateExceptionIfClassIsAbstract() {
        // When:
        underTest.getResolverInstanceInt(AbstractClass.class);

        // Then: expect exception
    }

    @Test(expected = IllegalStateException.class)
    public void testGetResolverInstanceIntShouldThrowIllegalStateExceptionIfDefaultConstructorThrowsException() {
        // When:
        underTest.getResolverInstanceInt(ExceptionInDefaultConstructor.class);

        // Then: expect exception
    }

    @Test
    public void testGetResolverInstanceIntShouldReturnInstanceIfDefaultConstructorIsAvailableEvenIfPrivate() {
        // Given:

        // When:
        DataProviderMethodResolver result = underTest.getResolverInstanceInt(PrivateDefaultConstructor.class);

        // Then:
        assertThat(result).isNotNull().isInstanceOf(PrivateDefaultConstructor.class);
    }

    // -- helper classes to test with ------------------------------------------------------------------------------------------------------

    private static class NoDefaultConstructor implements DataProviderMethodResolver {
        @SuppressWarnings("unused")
        public NoDefaultConstructor(String a) {
            // unused
        }

        @Override
        public FrameworkMethod resolve(FrameworkMethod testMethod, UseDataProvider useDataProvider) {
            return null;
        }
    }

    private static abstract class AbstractClass implements DataProviderMethodResolver {
        // unused
    }

    private static class ExceptionInDefaultConstructor implements DataProviderMethodResolver {
        @SuppressWarnings("unused")
        public ExceptionInDefaultConstructor() {
            throw new NumberFormatException();
        }

        @Override
        public FrameworkMethod resolve(FrameworkMethod testMethod, UseDataProvider useDataProvider) {
            return null;
        }
    }

    private static class PrivateDefaultConstructor implements DataProviderMethodResolver {
        @SuppressWarnings("unused")
        private PrivateDefaultConstructor() {
            // unused
        }

        @Override
        public FrameworkMethod resolve(FrameworkMethod testMethod, UseDataProvider useDataProvider) {
            return null;
        }
    }
}
