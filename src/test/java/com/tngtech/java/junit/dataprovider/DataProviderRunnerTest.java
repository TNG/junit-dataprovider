package com.tngtech.java.junit.dataprovider;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.tngtech.java.junit.dataprovider.internal.DataConverter;
import com.tngtech.java.junit.dataprovider.internal.TestGenerator;
import com.tngtech.java.junit.dataprovider.internal.TestValidator;
import com.tngtech.test.java.junit.dataprovider.category.CategoryOne;

public class DataProviderRunnerTest extends BaseTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
    private FrameworkMethod testMethod;
    @Mock
    private FrameworkMethod dataProviderMethod;
    @Mock
    private UseDataProvider useDataProvider;
    @Mock
    private DataProvider dataProvider;

    @Before
    public void setup() throws Exception {
        underTest = new DataProviderRunner(DataProviderRunnerTest.class);

        MockitoAnnotations.initMocks(this);
        underTest.dataConverter = dataConverter; // override default dataConverter
        underTest.testValidator = testValidator;
        underTest.testGenerator = testGenerator;
        underTest.testGenerator = frameworkMethodGenerator;
        doReturn(testClass).when(underTest).getTestClassInt();

        doReturn(anyMethod()).when(testMethod).getMethod();
        doReturn(anyMethod()).when(dataProviderMethod).getMethod();
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
    public void testValidateTestMethodsShouldAddErrorIfDataProviderMethodNotFoundForMethodWithUseDataProvider() {
        // Given:
        String dataProviderName = "notFoundDataProvider";

        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(UseDataProvider.class);
        doReturn(null).when(underTest).getDataProviderMethod(testMethod);
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(dataProviderName).when(useDataProvider).value();

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase("no such data provider");

        verifyZeroInteractions(testValidator);
    }

    @Test
    public void testValidateTestMethodsShouldCallTestValidatorValidateDataProviderMethodIfDataProviderMethodFound() {
        // Given:
        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(UseDataProvider.class);
        doReturn(dataProviderMethod).when(underTest).getDataProviderMethod(testMethod);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        verify(testValidator).validateDataProviderMethod(dataProviderMethod, errors);
        verifyNoMoreInteractions(testValidator);
    }

    @Test
    public void testValidateTestMethodsShouldWorkCorrectlyForMultipleMethodsAnnotatedWithUseDataProvider() {
        // Given:
        String dataProviderName = "notFoundDataProvider3";

        FrameworkMethod testMethod2 = mock(FrameworkMethod.class);
        FrameworkMethod testMethod3 = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod2 = mock(FrameworkMethod.class);

        doReturn(asList(testMethod, testMethod2, testMethod3)).when(testClass).getAnnotatedMethods(
                UseDataProvider.class);
        doReturn(dataProviderMethod).when(underTest).getDataProviderMethod(testMethod);
        doReturn(dataProviderMethod2).when(underTest).getDataProviderMethod(testMethod2);
        doReturn(null).when(underTest).getDataProviderMethod(testMethod3);

        doReturn(useDataProvider).when(testMethod3).getAnnotation(UseDataProvider.class);
        doReturn(dataProviderName).when(useDataProvider).value();

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase("no such data provider");

        verify(testValidator).validateDataProviderMethod(dataProviderMethod, errors);
        verify(testValidator).validateDataProviderMethod(dataProviderMethod2, errors);
        verifyNoMoreInteractions(testMethod);
    }

    @Test
    public void testClassBlockReturnNewStatement() {
        // Given:
        RunNotifier notifier = mock(RunNotifier.class);

        // When:
        Statement result = underTest.classBlock(notifier);

        // Then:
        assertThat(result).isNotNull();
        verifyZeroInteractions(notifier);
    }

    @Test
    public void testClassBlockReturnedStatementShouldRethrowKeptFailureOnEvaluate() throws Throwable {
        // Given:
        underTest.failure = new Throwable();

        RunNotifier notifier = mock(RunNotifier.class);
        Statement statement = underTest.classBlock(notifier);

        expectedException.expect(sameInstance(underTest.failure));

        // When:
        statement.evaluate();

        // Then:
        verifyZeroInteractions(notifier);
    }

    @Test
    public void testClassBlockShouldEvaluateChildrenInvokerStatementIfNoFailureIsKept() throws Throwable {
        // Given:
        underTest.failure = null;
        final AtomicBoolean evaluatedStatement = new AtomicBoolean(false);

        RunNotifier notifier = mock(RunNotifier.class);
        doReturn(new Statement() {
            @Override
            public void evaluate() {
                evaluatedStatement.set(true);
            }
        }).when(underTest).childrenInvoker(notifier);
        Statement statement = underTest.classBlock(notifier); // must be after spying childrenInvoker(...)

        // When:
        statement.evaluate();

        // Then:
        assertThat(evaluatedStatement.get()).isTrue();
        verifyZeroInteractions(notifier);
    }


    public void testComputeTestMethodsShouldCallGenerateExplodedTestMethodsAndCacheResultIfCalledTheFirstTime() {
        // Given:
        underTest.computedTestMethods = null;
        doReturn(new ArrayList<FrameworkMethod>()).when(underTest).generateExplodedTestMethodsFor(
                anyListOf(FrameworkMethod.class));

        // When:
        List<FrameworkMethod> result = underTest.computeTestMethods();

        // Then:
        assertThat(result).isEqualTo(underTest.computedTestMethods);

        verify(underTest).computeTestMethods();
        verify(underTest).generateExplodedTestMethodsFor(anyListOf(FrameworkMethod.class));
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

    @Test
    public void testFilterShouldNotThrowExceptionForJUnitCategoryFilter() throws Exception {
        // Given:
        Filter filter = new Categories.CategoryFilter(null, CategoryOne.class);

        // When:
        underTest.filter(filter);

        // Then: expect no exception
    }

    @Test(expected = NoTestsRemainException.class)
    public void testFilterShouldThrowNoTestRemainExceptionForNonBlacklistedAndRecognizableFilterHavingNoTestMethods()
            throws Exception {
        // Given:
        Filter filter = new Filter() {
            @Override
            public boolean shouldRun(Description description) {
                return true;
            }

            @Override
            public String describe() {
                return "testMethod(com.tngtech.java.junit.dataprovider.Test)";
            }
        };

        // When:
        underTest.filter(filter);

        // Then: expect no exception
    }

    @Test
    public void testInvokeBeforeClassShouldNotThrowButStoreFailure() throws Throwable {
        // Given:
        Throwable t = new Throwable();

        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(BeforeClass.class);
        doThrow(t).when(testMethod).invokeExplosively(null);

        // When:
        underTest.invokeBeforeClass();

        // Then:
        assertThat(underTest.failure).isSameAs(t);
    }

    @Test
    public void testInvokeBeforeClassShouldRunAllFoundBeforeClassMethods() throws Throwable {
        // Given:
        FrameworkMethod testMethod2 = mock(FrameworkMethod.class);
        FrameworkMethod testMethod3 = mock(FrameworkMethod.class);

        doReturn(asList(testMethod, testMethod2, testMethod3)).when(testClass).getAnnotatedMethods(BeforeClass.class);

        // When:
        underTest.invokeBeforeClass();

        // Then:
        InOrder inOrder = inOrder(testMethod, testMethod2, testMethod3);
        inOrder.verify(testMethod).invokeExplosively(null);
        inOrder.verify(testMethod2).invokeExplosively(null);
        inOrder.verify(testMethod3).invokeExplosively(null);
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
    public void testIsFilterBlackListedShouldReturnFalseForJunitPackagedFilter() {
        // Given:
        Filter filter = Filter.ALL;

        // When:
        boolean result = underTest.isFilterBlackListed(filter);

        // Then:
        assertThat(result).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDataProviderMethodShouldThrowIllegalArgumentExceptionIfTestMethodIsNull() {
        // Given:

        // When:
        underTest.getDataProviderMethod(null);

        // Then: expect exception
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

        doReturn(testClass).when(underTest).findDataProviderLocation(useDataProvider);

        // When:
        FrameworkMethod result = underTest.getDataProviderMethod(testMethod);

        // Then:
        assertThat(result).isNull();
    }

    @Test
    public void testGetDataProviderMethodShouldReturnDataProviderMethodIfItExists() {
        // Given:
        final String dataProviderMethodName = "availableDataProviderMethod";

        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(dataProviderMethodName).when(useDataProvider).value();

        doReturn(testClass).when(underTest).findDataProviderLocation(useDataProvider);

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.getDataProviderMethod(testMethod);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testFindDataProviderLocationShouldReturnTestClassForNotSetLocationInUseDataProviderAnnotation() {
        // Given:
        doReturn(new Class<?>[0]).when(useDataProvider).location();

        // When:
        TestClass result = underTest.findDataProviderLocation(useDataProvider);

        // Then:
        assertThat(result).isEqualTo(testClass);
    }

    @Test
    public void testFindDataProviderLocationShouldReturnTestClassContainingSetLocationInUseDataProviderAnnotation() {
        // Given:
        final Class<?> dataProviderLocation = DataProviderRunnerTest.class;

        doReturn(new Class<?>[] { dataProviderLocation }).when(useDataProvider).location();

        // When:
        TestClass result = underTest.findDataProviderLocation(useDataProvider);

        // Then:
        assertThat(result).isNotNull();
        // assertThat(result.getJavaClass()).isEqualTo(dataProviderLocation);
        assertThat(result.getName()).isEqualTo(dataProviderLocation.getName());
    }
}
