package com.tngtech.java.junit.dataprovider;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyListOf;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

import com.tngtech.java.junit.dataprovider.UseDataProvider.ResolveStrategy;
import com.tngtech.java.junit.dataprovider.internal.DataConverter;
import com.tngtech.java.junit.dataprovider.internal.DefaultDataProviderMethodResolver;
import com.tngtech.java.junit.dataprovider.internal.TestGenerator;
import com.tngtech.java.junit.dataprovider.internal.TestValidator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings({ "unchecked", "deprecation" })
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
    private TestClass testClass;
    @Mock
    private FrameworkMethod testMethod;
    @Mock
    private UseDataProvider useDataProvider;
    @Mock
    private DataProviderMethodResolver dataProviderMethodResolver;
    @Mock
    private FrameworkMethod dataProviderMethod;
    @Mock
    private DataProvider dataProvider;

    @BeforeClass
    public static void classSetup() throws Throwable {
        if (classSetupException != null) {
            throw classSetupException;
        }
    }

    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    @Before
    public void setup() throws Exception {
        classSetupException = null;

        underTest = new DataProviderRunner(DataProviderRunnerTest.class);

        MockitoAnnotations.initMocks(this);
        underTest.dataConverter = dataConverter;
        underTest.testValidator = testValidator;
        underTest.testGenerator = testGenerator;

        when(underTest.getTestClassInt()).thenReturn(testClass);

        when(testMethod.getAnnotation(UseDataProvider.class)).thenReturn(useDataProvider);
        when(testMethod.getMethod()).thenReturn(anyMethod());
        when(testMethod.getName()).thenReturn("testMethod");

        when(useDataProvider.value()).thenReturn(UseDataProvider.DEFAULT_VALUE);
        when(useDataProvider.resolver())
                .thenReturn((Class<? extends DataProviderMethodResolver>[]) new Class<?>[] { DataProviderMethodResolver.class });
    }

    @Test
    public void testDataProviderRunner() throws Exception {
        // Given:
        Class<?> clazz = DataProviderRunnerTest.class;

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

        when(underTest.computeTestMethods()).thenReturn(Collections.<FrameworkMethod>emptyList());

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
            @Override
            public Void answer(InvocationOnMock invocation) {
                ((List<Throwable>) invocation.getArguments()[0]).add(new Error());
                return null;
            }
        }).when(underTest).validateTestMethods(errors);

        when(underTest.computeTestMethods()).thenThrow(IllegalArgumentException.class);

        // When:
        underTest.validateInstanceMethods(errors);

        // Then:
        assertThat(errors).hasSize(1);
    }

    @Test
    public void testValidateInstanceMethodsShouldNotThrowExceptionIfNoErrorsExistAndTestMethodsAreAvailable() {
        // Given:
        List<Throwable> errors = new ArrayList<Throwable>();

        when(underTest.computeTestMethods()).thenReturn(singletonList(testMethod));

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
        when(testClass.getAnnotatedMethods(Test.class)).thenReturn(singletonList(testMethod));

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
        when(testClass.getAnnotatedMethods(Test.class)).thenReturn(asList(testMethod, testMethod2, testMethod3));

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
    public void testValidateTestMethodsShouldAddErrorIfDataProviderMethodNotFoundForMethodWithUseDataProviderUsingOnlyDefaultResolver() {
        // Given:
        when(testClass.getAnnotatedMethods(UseDataProvider.class)).thenReturn(singletonList(testMethod));
        doReturn(emptyList()).when(underTest).getDataProviderMethods(testMethod);
        when(testMethod.getAnnotation(UseDataProvider.class)).thenReturn(useDataProvider);
        when(useDataProvider.resolver())
                .thenReturn((Class<? extends DataProviderMethodResolver>[]) new Class<?>[] { DefaultDataProviderMethodResolver.class });

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
    public void testValidateTestMethodsShouldAddErrorIfDataProviderMethodNotFoundForMethodWithUseDataProviderUsingAdditionalCustomResolver() {
        // Given:
        when(testClass.getAnnotatedMethods(UseDataProvider.class)).thenReturn(singletonList(testMethod));
        doReturn(emptyList()).when(underTest).getDataProviderMethods(testMethod);
        when(testMethod.getAnnotation(UseDataProvider.class)).thenReturn(useDataProvider);
        when(useDataProvider.resolver())
                .thenReturn((Class<? extends DataProviderMethodResolver>[]) new Class<?>[] { null, DefaultDataProviderMethodResolver.class });

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
        when(testClass.getAnnotatedMethods(UseDataProvider.class)).thenReturn(singletonList(testMethod));
        when(underTest.getDataProviderMethods(testMethod)).thenReturn(singletonList(dataProviderMethod));

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then: expect exception
    }

    @Test
    public void testValidateTestMethodsShouldCallTestValidatorValidateDataProviderMethodIfDataProviderMethodFound() {
        // Given:
        when(testClass.getAnnotatedMethods(UseDataProvider.class)).thenReturn(singletonList(testMethod));
        doReturn(singletonList(dataProviderMethod)).when(underTest).getDataProviderMethods(testMethod);
        when(dataProviderMethod.getAnnotation(DataProvider.class)).thenReturn(dataProvider);

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
        FrameworkMethod testMethod2 = mock(FrameworkMethod.class);
        UseDataProvider useDataProvider2 = mock(UseDataProvider.class);
        FrameworkMethod dataProviderMethod2 = mock(FrameworkMethod.class);
        DataProvider dataProvider2 = mock(DataProvider.class);

        FrameworkMethod testMethod3 = mock(FrameworkMethod.class);
        UseDataProvider useDataProvider3 = mock(UseDataProvider.class);

        when(testClass.getAnnotatedMethods(UseDataProvider.class)).thenReturn(asList(testMethod, testMethod2, testMethod3));

        when(testMethod.getAnnotation(UseDataProvider.class)).thenReturn(useDataProvider);
        doReturn(singletonList(dataProviderMethod)).when(underTest).getDataProviderMethods(testMethod);
        when(dataProviderMethod.getAnnotation(DataProvider.class)).thenReturn(dataProvider);

        when(testMethod2.getAnnotation(UseDataProvider.class)).thenReturn(useDataProvider2);
        doReturn(singletonList(dataProviderMethod2)).when(underTest).getDataProviderMethods(testMethod2);
        when(dataProviderMethod2.getAnnotation(DataProvider.class)).thenReturn(dataProvider2);

        when(testMethod3.getAnnotation(UseDataProvider.class)).thenReturn(useDataProvider3);
        doReturn(emptyList()).when(underTest).getDataProviderMethods(testMethod3);
        when(useDataProvider3.resolver())
                .thenReturn((Class<? extends DataProviderMethodResolver>[]) new Class<?>[] { DataProviderMethodResolver.class });

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).containsIgnoringCase("no valid dataprovider found for test");

        verify(testValidator).validateDataProviderMethod(dataProviderMethod, dataProvider, errors);
        verify(testValidator).validateDataProviderMethod(dataProviderMethod2, dataProvider2, errors);
        verifyNoMoreInteractions(testValidator);
    }

    @Test
    public void testComputeTestMethodsShouldCallGenerateExplodedTestMethodsAndCacheResultIfCalledTheFirstTime() {
        // Given:
        underTest.computedTestMethods = null;
        doReturn(new ArrayList<FrameworkMethod>()).when(underTest).generateExplodedTestMethodsFor(anyListOf(FrameworkMethod.class));

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
        assertThat(underTest.getDescription().getChildren()).isNotEmpty();
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
    public void testGenerateExplodedTestMethodsForShouldCallTestGeneratorWithNotFoundDataProviderMethodAndAddResult() {
        // Given:
        doReturn(singletonList(null)).when(underTest).getDataProviderMethods(testMethod);
        when(testGenerator.generateExplodedTestMethodsFor(testMethod, null)).thenReturn(singletonList(testMethod));

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(singletonList(testMethod));

        // Then:
        assertThat(result).containsOnly(testMethod);

        verify(testGenerator).generateExplodedTestMethodsFor(testMethod, null);
        verifyNoMoreInteractions(testGenerator);
    }

    @Test
    public void testGenerateExplodedTestMethodsForShouldCallTestGeneratorWithFoundDataProviderMethodAndAddResult() {
        // Given:
        doReturn(singletonList(dataProviderMethod)).when(underTest).getDataProviderMethods(testMethod);

        List<FrameworkMethod> explodedMethods = new ArrayList<FrameworkMethod>();
        explodedMethods.add(mock(FrameworkMethod.class));
        explodedMethods.add(mock(FrameworkMethod.class));
        when(testGenerator.generateExplodedTestMethodsFor(testMethod, dataProviderMethod)).thenReturn(explodedMethods);

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(singletonList(testMethod));

        // Then:
        assertThat(result).hasSize(2).containsAll(explodedMethods);

        verify(testGenerator).generateExplodedTestMethodsFor(testMethod, dataProviderMethod);
        verifyNoMoreInteractions(testGenerator);
    }

    @Test
    public void testGenerateExplodedTestMethodsForShouldCallTestGeneratorForAllTestMethodsAndAddResult() {
        // Given:
        FrameworkMethod testMethod2 = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod21 = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod22 = mock(FrameworkMethod.class);

        doReturn(singletonList(dataProviderMethod)).when(underTest).getDataProviderMethods(testMethod);
        doReturn(asList(dataProviderMethod21, dataProviderMethod22)).when(underTest).getDataProviderMethods(testMethod2);

        List<FrameworkMethod> explodedMethods = new ArrayList<FrameworkMethod>();
        explodedMethods.add(mock(FrameworkMethod.class));
        explodedMethods.add(mock(FrameworkMethod.class));
        when(testGenerator.generateExplodedTestMethodsFor(testMethod, dataProviderMethod)).thenReturn(explodedMethods);

        List<FrameworkMethod> explodedMethods21 = new ArrayList<FrameworkMethod>();
        explodedMethods21.add(mock(FrameworkMethod.class));
        explodedMethods21.add(mock(FrameworkMethod.class));
        explodedMethods21.add(mock(FrameworkMethod.class));
        when(testGenerator.generateExplodedTestMethodsFor(testMethod2, dataProviderMethod21)).thenReturn(explodedMethods21);

        List<FrameworkMethod> explodedMethods22 = new ArrayList<FrameworkMethod>();
        explodedMethods22.add(mock(FrameworkMethod.class));
        explodedMethods22.add(mock(FrameworkMethod.class));
        when(testGenerator.generateExplodedTestMethodsFor(testMethod2, dataProviderMethod22)).thenReturn(explodedMethods22);

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(asList(testMethod, testMethod2));

        // Then:
        assertThat(result).hasSize(7).containsAll(explodedMethods).containsAll(explodedMethods21).containsAll(explodedMethods22);

        verify(testGenerator).generateExplodedTestMethodsFor(testMethod, dataProviderMethod);
        verify(testGenerator).generateExplodedTestMethodsFor(testMethod2, dataProviderMethod21);
        verify(testGenerator).generateExplodedTestMethodsFor(testMethod2, dataProviderMethod22);
        verifyNoMoreInteractions(testGenerator);
    }

    @Test
    public void testGetDataProviderMethodShouldInitializeMapUsedForCaching() {
        // Given:
        when(testMethod.getAnnotation(UseDataProvider.class)).thenReturn(null);

        underTest.dataProviderMethods = null;

        // When:
        underTest.getDataProviderMethods(testMethod);

        // Then:
        assertThat(underTest.dataProviderMethods).isNotNull();
    }

    @Test
    public void testGetDataProviderMethodShouldReturnedChachedValueIfExists() {
        // Given:
        final List<FrameworkMethod> expected = asList(dataProviderMethod, testMethod);

        underTest.dataProviderMethods = new HashMap<FrameworkMethod, List<FrameworkMethod>>();
        underTest.dataProviderMethods.put(testMethod, expected);

        // When:
        List<FrameworkMethod> result = underTest.getDataProviderMethods(testMethod);

        // Then:
        assertThat(result).isSameAs(expected);
    }

    @Test
    public void testGetDataProviderMethodShouldReturnSingletonListContainingNullForNotFoundUseDataProviderAnnotation() {
        // Given:
        when(testMethod.getAnnotation(UseDataProvider.class)).thenReturn(null);

        // When:
        List<FrameworkMethod> result = underTest.getDataProviderMethods(testMethod);

        // Then:
        assertThat(result).hasSize(1).containsNull();
        assertThat(underTest.dataProviderMethods).containsEntry(testMethod, result);
    }

    @Test
    public void testGetDataProviderMethodShouldReturnEmptyListForNotFoundDataProviderMethod() {
        // Given:
        when(testMethod.getAnnotation(UseDataProvider.class)).thenReturn(useDataProvider);
        when(useDataProvider.value()).thenReturn("notAvailableDataProviderMethod");

        when(testClass.getAnnotatedMethods(DataProvider.class)).thenReturn(singletonList(dataProviderMethod));
        when(dataProviderMethod.getName()).thenReturn("availableDataProviderMethod");

        doReturn(dataProviderMethodResolver).when(underTest).getResolverInstanceInt(any(Class.class));

        // When:
        List<FrameworkMethod> result = underTest.getDataProviderMethods(testMethod);

        // Then:
        assertThat(result).isEmpty();
        assertThat(underTest.dataProviderMethods).containsEntry(testMethod, result);
    }

    @Test
    public void testGetDataProviderMethodShouldReturnEmptyListIfUseDataProviderResolversAreEmpty() {
        // Given:
        when(testMethod.getAnnotation(UseDataProvider.class)).thenReturn(useDataProvider);

        doReturn(dataProviderMethodResolver).when(underTest).getResolverInstanceInt(any(Class.class));

        // When:
        List<FrameworkMethod> result = underTest.getDataProviderMethods(testMethod);

        // Then:
        assertThat(result).isEmpty();
        assertThat(underTest.dataProviderMethods).containsEntry(testMethod, result);
    }

    @Test
    public void testGetDataProviderMethodShouldReturnFirstNotEmptyListIfResolveStrategyIsUntilFirstMatchAndMultipleResolversWouldMatch() {
        // Given:
        final DataProviderMethodResolver resolver2 = mock(DataProviderMethodResolver.class);
        final DataProviderMethodResolver resolver3 = mock(DataProviderMethodResolver.class);

        final List<FrameworkMethod> expected2 = Arrays.asList(mock(FrameworkMethod.class), mock(FrameworkMethod.class));
        final List<FrameworkMethod> expected3 = singletonList(mock(FrameworkMethod.class));

        when(testMethod.getAnnotation(UseDataProvider.class)).thenReturn(useDataProvider);
        when(useDataProvider.resolver()).thenReturn((Class<? extends DataProviderMethodResolver>[]) new Class<?>[] {
                DataProviderMethodResolver.class, DataProviderMethodResolver.class, DataProviderMethodResolver.class });
        when(useDataProvider.resolveStrategy()).thenReturn(ResolveStrategy.UNTIL_FIRST_MATCH);

        doReturn(dataProviderMethodResolver, resolver2, resolver3).when(underTest).getResolverInstanceInt(any(Class.class));

        when(dataProviderMethodResolver.resolve(testMethod, useDataProvider)).thenReturn(Collections.<FrameworkMethod>emptyList());
        when(resolver2.resolve(testMethod, useDataProvider)).thenReturn(expected2);
        when(resolver3.resolve(testMethod, useDataProvider)).thenReturn(expected3);

        // When:
        List<FrameworkMethod> result = underTest.getDataProviderMethods(testMethod);

        // Then:
        assertThat(result).containsExactlyElementsOf(expected2);
        assertThat(underTest.dataProviderMethods).containsEntry(testMethod, result);
    }

    @Test
    public void testGetDataProviderMethodShouldReturnFirstNotEmptyListIfResolveStrategyIsAggregateAllMatchesAndMultipleResolversWouldMatch() {
        // Given:
        final DataProviderMethodResolver resolver2 = mock(DataProviderMethodResolver.class);
        final DataProviderMethodResolver resolver3 = mock(DataProviderMethodResolver.class);

        final List<FrameworkMethod> expected2 = Arrays.asList(mock(FrameworkMethod.class), mock(FrameworkMethod.class));
        final List<FrameworkMethod> expected3 = singletonList(mock(FrameworkMethod.class));

        when(testMethod.getAnnotation(UseDataProvider.class)).thenReturn(useDataProvider);
        when(useDataProvider.resolver()).thenReturn((Class<? extends DataProviderMethodResolver>[]) new Class<?>[] {
                DataProviderMethodResolver.class, DataProviderMethodResolver.class, DataProviderMethodResolver.class });
        when(useDataProvider.resolveStrategy()).thenReturn(ResolveStrategy.AGGREGATE_ALL_MATCHES);

        doReturn(dataProviderMethodResolver, resolver2, resolver3).when(underTest).getResolverInstanceInt(any(Class.class));

        when(dataProviderMethodResolver.resolve(testMethod, useDataProvider)).thenReturn(Collections.<FrameworkMethod>emptyList());
        when(resolver2.resolve(testMethod, useDataProvider)).thenReturn(expected2);
        when(resolver3.resolve(testMethod, useDataProvider)).thenReturn(expected3);

        // When:
        List<FrameworkMethod> result = underTest.getDataProviderMethods(testMethod);

        // Then:
        assertThat(result).hasSize(3).containsAll(expected2).containsAll(expected3);
        assertThat(underTest.dataProviderMethods).containsEntry(testMethod, result);
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
        assertThat(result).isInstanceOf(PrivateDefaultConstructor.class);
    }

    // -- helper classes to test with ------------------------------------------------------------------------------------------------------

    private static class NoDefaultConstructor implements DataProviderMethodResolver {
        @SuppressWarnings("unused")
        public NoDefaultConstructor(String a) {
            // unused
        }

        @Override
        public List<FrameworkMethod> resolve(FrameworkMethod testMethod, UseDataProvider useDataProvider) {
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
        public List<FrameworkMethod> resolve(FrameworkMethod testMethod, UseDataProvider useDataProvider) {
            return null;
        }
    }

    private static class PrivateDefaultConstructor implements DataProviderMethodResolver {
        private PrivateDefaultConstructor() {
            // unused
        }

        @Override
        public List<FrameworkMethod> resolve(FrameworkMethod testMethod, UseDataProvider useDataProvider) {
            return null;
        }
    }
}
