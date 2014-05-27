package com.tngtech.java.junit.dataprovider;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.tngtech.test.java.junit.dataprovider.category.CategoryOne;

public class DataProviderRunnerTest extends BaseTest {

    @Spy
    private DataProviderRunner underTest;

    @Mock
    private TestClass testClass;

    @Before
    public void setup() throws Exception {
        underTest = new DataProviderRunner(DataProviderRunnerTest.class);

        MockitoAnnotations.initMocks(this);
        doReturn(testClass).when(underTest).getTestClassInt();
    }

    @SuppressWarnings("unchecked")
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

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTestMethodsShouldThrowIllegalArgumentExceptionIfArgumentIsNull() {
        // Given:

        // When:
        underTest.validateTestMethods(null);

        // Then: expect exception
    }

    @Test
    public void testValidateTestMethodsShouldCheckForPublicVoidNoArgIfNormalTestMethod() {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);

        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(Test.class);
        doReturn(null).when(testMethod).getAnnotation(UseDataProvider.class);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        verify(testMethod).getAnnotation(DataProvider.class);
        verify(testMethod).getAnnotation(UseDataProvider.class);
        verify(testMethod).validatePublicVoidNoArg(false, errors);
        verifyNoMoreInteractions(testMethod);
    }

    @Test
    public void testValidateTestMethodsShouldCheckForPublicVoidIfUseDataProviderTestMethod() {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        UseDataProvider useDataProvider = mock(UseDataProvider.class);

        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(Test.class);
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        verify(testMethod).getAnnotation(DataProvider.class);
        verify(testMethod).getAnnotation(UseDataProvider.class);
        verify(testMethod).validatePublicVoid(false, errors);
        verifyNoMoreInteractions(testMethod);
    }

    @Test
    public void testValidateTestMethodsShouldCheckForPublicVoidIfDataProviderTestMethod() {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        DataProvider dataProvider = mock(DataProvider.class);

        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(Test.class);
        doReturn(dataProvider).when(testMethod).getAnnotation(DataProvider.class);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        verify(testMethod).getAnnotation(DataProvider.class);
        verify(testMethod).getAnnotation(UseDataProvider.class);
        verify(testMethod).validatePublicVoid(false, errors);
        verifyNoMoreInteractions(testMethod);
    }

    @Test
    public void testValidateTestMethodsShouldAddErrorIfDataProviderAndUseDataProviderTestMethod() {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        DataProvider dataProvider = mock(DataProvider.class);
        UseDataProvider useDataProvider = mock(UseDataProvider.class);

        doReturn("test1").when(testMethod).getName();
        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(Test.class);
        doReturn(dataProvider).when(testMethod).getAnnotation(DataProvider.class);
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).isInstanceOf(Exception.class);
        assertThat(errors.get(0).getMessage()).matches(
                "Method test1\\(\\) should either have @UseDataProvider.* or @DataProvider.* annotation");

        verify(testMethod).getAnnotation(DataProvider.class);
        verify(testMethod).getAnnotation(UseDataProvider.class);
        verify(testMethod).getName();
        verifyNoMoreInteractions(testMethod);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateDataProviderMethodsShouldThrowIllegalArgumentExceptionIfArgumentIsNull() {
        // Given:

        // When:
        underTest.validateDataProviderMethods(null);

        // Then: expect exception
    }

    @Test
    public void testValidateDataProviderMethodsShouldAddErrorIfDataProviderMethodDoesNotExist() {
        // Given:
        final String dataProviderName = "dataProviderMethodName";

        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        UseDataProvider useDataProvider = mock(UseDataProvider.class);

        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(UseDataProvider.class);
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(dataProviderName).when(useDataProvider).value();
        doReturn(null).when(underTest).getDataProviderMethod(testMethod);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateDataProviderMethods(errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase("no such data provider");
    }

    @Test
    public void testValidateDataProviderMethodsShouldCallValidateDataProviderMethodForEachDataProvider() {
        // Given:
        FrameworkMethod testMethod1 = mock(FrameworkMethod.class);
        FrameworkMethod testMethod2 = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod1 = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod2 = mock(FrameworkMethod.class);

        doReturn(asList(testMethod1, testMethod2)).when(testClass).getAnnotatedMethods(UseDataProvider.class);
        doReturn(dataProviderMethod1).when(underTest).getDataProviderMethod(testMethod1);
        doReturn(dataProviderMethod2).when(underTest).getDataProviderMethod(testMethod2);
        doReturn(mock(UseDataProvider.class)).when(testMethod1).getAnnotation(UseDataProvider.class);
        doReturn(mock(UseDataProvider.class)).when(testMethod2).getAnnotation(UseDataProvider.class);

        doNothing().when(underTest).validateDataProviderMethod(any(FrameworkMethod.class), anyListOf(Throwable.class));

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateDataProviderMethods(errors);

        // Then:
        verify(underTest).validateDataProviderMethod(dataProviderMethod1, errors);
        verify(underTest).validateDataProviderMethod(dataProviderMethod2, errors);
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
    public void testGenerateExplodedTestMethodsForShouldReturnOriginalTestMethodIfNoDataProviderMethod() {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);

        doReturn(null).when(underTest).getDataProviderMethod(testMethod);

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(asList(testMethod));

        // Then:
        assertThat(result).containsOnly(testMethod);
    }

    @Test
    public void testGenerateExplodedTestMethodsForShouldReturnExplodedTestMethodsForValidDataProvider() {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        doReturn(dataProviderMethod).when(underTest).getDataProviderMethod(testMethod);

        List<FrameworkMethod> explodedMethods = new ArrayList<FrameworkMethod>();
        explodedMethods.add(mock(FrameworkMethod.class));
        explodedMethods.add(mock(FrameworkMethod.class));
        doReturn(explodedMethods).when(underTest).explodeTestMethod(testMethod, dataProviderMethod);

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(asList(testMethod));

        // Then:
        assertThat(result).hasSize(2).containsAll(explodedMethods);
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
        FrameworkMethod testMethod = mock(FrameworkMethod.class);

        doReturn(null).when(testMethod).getAnnotation(UseDataProvider.class);

        // When:
        FrameworkMethod result = underTest.getDataProviderMethod(testMethod);

        // Then:
        assertThat(result).isNull();
    }

    @Test
    public void testGetDataProviderMethodShouldReturnNullForNotFoundDataProviderMethod() {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        UseDataProvider useDataProvider = mock(UseDataProvider.class);

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

        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        UseDataProvider useDataProvider = mock(UseDataProvider.class);

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
        UseDataProvider useDataProvider = mock(UseDataProvider.class);
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

        UseDataProvider useDataProvider = mock(UseDataProvider.class);
        doReturn(new Class<?>[] { dataProviderLocation }).when(useDataProvider).location();

        // When:
        TestClass result = underTest.findDataProviderLocation(useDataProvider);

        // Then:
        assertThat(result).isNotNull();
        // assertThat(result.getJavaClass()).isEqualTo(dataProviderLocation);
        assertThat(result.getName()).isEqualTo(dataProviderLocation.getName());
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfItIsNotPublic() {

        // Given:
        String dataProviderName = "dataProviderNotPublic";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("nonPublicDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase("must be public");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfItIsNotStatic() {
        // Given:
        String dataProviderName = "dataProviderNotStatic";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("nonStaticDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase("must be static");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfItRequiresParameters() {
        // Given:
        String dataProviderName = "dataProviderNonNoArg";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("nonNoArgDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must have no parameters");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfItReturnsString() {
        // Given:
        String dataProviderName = "dataProviderReturningString";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("stringReturningDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either return Object[][] or List<List<Object>>");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfItReturnsListOfObject() {
        // Given:
        String dataProviderName = "dataProviderReturningListOfObject";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("listReturningDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either return Object[][] or List<List<Object>>");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfItReturnsListOfIterable() {
        // Given:
        String dataProviderName = "dataProviderReturningListOfIterable";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("listOfIterableReturningDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either return Object[][] or List<List<Object>>");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfItReturnsIterableOfIterable() {
        // Given:
        String dataProviderName = "dataProviderIterableOfIterable";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("iterableOfIterableReturningDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either return Object[][] or List<List<Object>>");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfItReturnsSetOfSet() {
        // Given:
        String dataProviderName = "dataProviderReturningSetOfSet";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("setOfSetReturningDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either return Object[][] or List<List<Object>>");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfItReturnsTwoArgList() {
        // Given:
        String dataProviderName = "dataProviderReturningTwoArgList";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("twoArgListReturningDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either return Object[][] or List<List<Object>>");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorsIfItNonStaticAndNonPublicAndNonNoArg() {
        // Given:
        String dataProviderName = "dataProviderNonPublicNonStaticNonNoArg";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("nonPublicNonStaticNonNoArgDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(3);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase("must be public");
        assertThat(errors.get(1).getMessage()).contains(dataProviderName).containsIgnoringCase("must be static");
        assertThat(errors.get(2).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must have no parameters");
    }

    @Test
    public void testValidateDataProviderMethodShouldReturnTrueIfItIsPublicStaticNoArgAndReturnsObjectArrayArray() {
        // Given:
        String dataProviderName = "dataProviderObjectArrayArray";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("validDataProviderMethodArray")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).isEmpty();
    }

    @Test
    public void testValidateDataProviderMethodShouldReturnTrueIfItIsPublicStaticNoArgAndReturnsListListObject() {
        // Given:
        String dataProviderName = "dataProviderListOfListOfObject";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("validDataProviderMethodList")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).isEmpty();
    }

    @Test
    public void testValidateDataProviderMethodShouldReturnTrueIfItIsPublicStaticNoArgAndReturnsSubListSubListObject() {
        // Given:
        String dataProviderName = "dataProviderSubListOfSubListOfObject";

        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("validDataProviderMethodSubList")).when(dataProviderMethod).getMethod();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).isEmpty();
    }

    @Test(expected = Error.class)
    public void testExplodeTestMethodsUseDataProviderShouldThrowErrorIfDataProviderMethodThrowsException()
            throws Throwable {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        doThrow(NullPointerException.class).when(dataProviderMethod).invokeExplosively(null);

        // When:
        underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then: expect exception
    }

    @Test(expected = Error.class)
    public void testExplodeTestMethodsUseDataProviderShouldThrowErrorIfDataProviderMethodReturnsNull() throws Throwable {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        doReturn(null).when(dataProviderMethod).invokeExplosively(null);

        // When:
        underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then: expect exception
    }

    @Test(expected = Error.class)
    public void testExplodeTestMethodsUseDataProviderShouldThrowErrorIfDataProviderMethodReturnsEmptyObjectArrayArray()
            throws Throwable {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        doReturn(new Object[0][0]).when(dataProviderMethod).invokeExplosively(null);

        // When:
        underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then: expect exception
    }

    @Test
    public void testExplodeTestMethodsUseDataProviderShouldReturnOneDataProviderFrameworkMethodIfDataProviderMethodArrayReturnsOneRow()
            throws Throwable {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        Object[][] dataProviderMethodResult = new Object[][] { { 1, 2, 3 } };
        doReturn(dataProviderMethodResult).when(dataProviderMethod).invokeExplosively(null);

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then:
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isInstanceOf(DataProviderFrameworkMethod.class);

        DataProviderFrameworkMethod actual = (DataProviderFrameworkMethod) result.get(0);
        assertThat(actual.idx).isEqualTo(0);
        assertThat(actual.parameters).isEqualTo(dataProviderMethodResult[0]);
    }

    @Test
    public void testExplodeTestMethodsUseDataProviderShouldReturnOneDataProviderFrameworkMethodIfDataProviderMethodListReturnsOneRow()
            throws Throwable {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        @SuppressWarnings("unchecked")
        List<List<Object>> dataProviderMethodResult = list(this.<Object> list(10L, 11, "12"));
        doReturn(dataProviderMethodResult).when(dataProviderMethod).invokeExplosively(null);

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then:
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isInstanceOf(DataProviderFrameworkMethod.class);

        DataProviderFrameworkMethod actual = (DataProviderFrameworkMethod) result.get(0);
        assertThat(actual.idx).isEqualTo(0);
        assertThat(actual.parameters).isEqualTo(dataProviderMethodResult.get(0).toArray());
    }

    @Test
    public void testExplodeTestMethodsUseDataProviderShouldReturnMultipleDataProviderFrameworkMethodIfDataProviderMethodArrayReturnsMultipleRow()
            throws Throwable {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        Object[][] dataProviderMethodResult = new Object[][] { { 1, "2", 3L }, { 4, "5", 6L }, { 7, "8", 9L } };
        doReturn(dataProviderMethodResult).when(dataProviderMethod).invokeExplosively(null);

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then:
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isInstanceOf(DataProviderFrameworkMethod.class);

        DataProviderFrameworkMethod actual0 = (DataProviderFrameworkMethod) result.get(0);
        assertThat(actual0.idx).isEqualTo(0);
        assertThat(actual0.parameters).isEqualTo(dataProviderMethodResult[0]);

        assertThat(result.get(1)).isInstanceOf(DataProviderFrameworkMethod.class);

        DataProviderFrameworkMethod actual1 = (DataProviderFrameworkMethod) result.get(1);
        assertThat(actual1.idx).isEqualTo(1);
        assertThat(actual1.parameters).isEqualTo(dataProviderMethodResult[1]);

        assertThat(result.get(0)).isInstanceOf(DataProviderFrameworkMethod.class);

        DataProviderFrameworkMethod actual2 = (DataProviderFrameworkMethod) result.get(2);
        assertThat(actual2.idx).isEqualTo(2);
        assertThat(actual2.parameters).isEqualTo(dataProviderMethodResult[2]);
    }

    @Test
    public void testExplodeTestMethodsUseDataProviderShouldReturnMultipleDataProviderFrameworkMethodIfDataProviderMethodListReturnsMultipleRow()
            throws Throwable {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        @SuppressWarnings("unchecked")
        List<List<?>> dataProviderMethodResult = list(this.<Object> list(1, "a"), list(3, "b"), list(5, "c"));
        doReturn(dataProviderMethodResult).when(dataProviderMethod).invokeExplosively(null);

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then:
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isInstanceOf(DataProviderFrameworkMethod.class);

        DataProviderFrameworkMethod actual0 = (DataProviderFrameworkMethod) result.get(0);
        assertThat(actual0.idx).isEqualTo(0);
        assertThat(actual0.parameters).isEqualTo(dataProviderMethodResult.get(0).toArray());

        assertThat(result.get(1)).isInstanceOf(DataProviderFrameworkMethod.class);

        DataProviderFrameworkMethod actual1 = (DataProviderFrameworkMethod) result.get(1);
        assertThat(actual1.idx).isEqualTo(1);
        assertThat(actual1.parameters).isEqualTo(dataProviderMethodResult.get(1).toArray());

        assertThat(result.get(0)).isInstanceOf(DataProviderFrameworkMethod.class);

        DataProviderFrameworkMethod actual2 = (DataProviderFrameworkMethod) result.get(2);
        assertThat(actual2.idx).isEqualTo(2);
        assertThat(actual2.parameters).isEqualTo(dataProviderMethodResult.get(2).toArray());
    }

    @Test(expected = Error.class)
    public void testExplodeTestMethodsDataProviderShouldThrowErrorIfDataProviderValueReturnsAnEmptyArray() {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        DataProvider dataProvider = mock(DataProvider.class);

        doReturn(new String[0]).when(dataProvider).value();

        // When:
        underTest.explodeTestMethod(testMethod, dataProvider);

        // Then: expect exception
    }

    @Test
    public void testIsFilterBlackListedShouldReturnFalseForJUnitPackagedFilter() {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        DataProvider dataProvider = mock(DataProvider.class);

        doReturn(getMethod("testStringString")).when(testMethod).getMethod();

        String[] dataProviderValueResult = new String[] { "foo, bar" };
        doReturn(dataProviderValueResult).when(dataProvider).value();

        // TODO init testMethod for required types

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProvider);

        // Then:
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isInstanceOf(DataProviderFrameworkMethod.class);

        DataProviderFrameworkMethod actual = (DataProviderFrameworkMethod) result.get(0);
        assertThat(actual.idx).isEqualTo(0);
        assertThat(actual.parameters).isEqualTo(new Object[] { "foo", "bar" });
    }

    @Test
    public void testExplodeTestMethodsDataProviderShouldReturnMultipleDataProviderFrameworkMethodIfDataProviderValueArrayReturnsMultipleRow() {
        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        DataProvider dataProvider = mock(DataProvider.class);

        doReturn(getMethod("testStringString")).when(testMethod).getMethod();

        String[] dataProviderValueResult = new String[] { "2a, foo", "3b, bar", "4c, baz" };
        doReturn(dataProviderValueResult).when(dataProvider).value();

        // When:
        List<FrameworkMethod> result = underTest.explodeTestMethod(testMethod, dataProvider);

        // Then:
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isInstanceOf(DataProviderFrameworkMethod.class);

        DataProviderFrameworkMethod actual0 = (DataProviderFrameworkMethod) result.get(0);
        assertThat(actual0.idx).isEqualTo(0);
        assertThat(actual0.parameters).isEqualTo(new Object[] { "2a", "foo" });

        assertThat(result.get(1)).isInstanceOf(DataProviderFrameworkMethod.class);

        DataProviderFrameworkMethod actual1 = (DataProviderFrameworkMethod) result.get(1);
        assertThat(actual1.idx).isEqualTo(1);
        assertThat(actual1.parameters).isEqualTo(new Object[] { "3b", "bar" });

        assertThat(result.get(0)).isInstanceOf(DataProviderFrameworkMethod.class);

        DataProviderFrameworkMethod actual2 = (DataProviderFrameworkMethod) result.get(2);
        assertThat(actual2.idx).isEqualTo(2);
        assertThat(actual2.parameters).isEqualTo(new Object[] { "4c", "baz" });
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

    @Test(expected = Error.class)
    public void testGetParametersShouldThrowErrorIfLengthOfSplitDataAndTargetTypesDiffer() {
        // Given:
        String data = "a";
        Class<?>[] parameterTypes = new Class[] { String.class, String.class };
        int rowIdx = 0;

        // When:
        underTest.getParameters(data, parameterTypes, rowIdx);

        // Then: expect exception
    }

    @Test
    public void testGetParametersShouldCorrectlyParseAllPrimitiveTypes() {
        // Given:
        String data = "true,1,c,2,3,4,5.5,6.6";
        Class<?>[] parameterTypes = new Class[] { boolean.class, byte.class, char.class, short.class, int.class,
                long.class, float.class, double.class };
        int rowIdx = 1;

        // When:
        Object[] result = underTest.getParameters(data, parameterTypes, rowIdx);

        // Then:
        assertThat(result).isEqualTo(new Object[] { true, (byte) 1, 'c', (short) 2, 3, 4L, 5.5f, 6.6d });
    }

    @Test
    public void testGetParametersShouldCorrectlyParseAllPrimitiveTypesAsJavaString() {
        // Given:
        String data = "-2014,-1.234567f,-901e-3";
        Class<?>[] parameterTypes = new Class[] { long.class, float.class, double.class };
        int rowIdx = 2;

        // When:
        Object[] result = underTest.getParameters(data, parameterTypes, rowIdx);

        // Then:
        assertThat(result).isEqualTo(new Object[] { -2014L, -1.234567f, -0.901d });
    }

    @Test
    public void testGetParametersShouldCorrectlyParseAllPrimitiveTypesEvenIfUntrimmed() {
        // Given:
        String data = "   false   ,    11    ,    z    ,  22       ,   33   ,44      ,   55.55     ,  66.66     ";
        Class<?>[] parameterTypes = new Class[] { boolean.class, byte.class, char.class, short.class, int.class,
                long.class, float.class, double.class };
        int rowIdx = 3;

        // When:
        Object[] result = underTest.getParameters(data, parameterTypes, rowIdx);

        // Then:
        assertThat(result).isEqualTo(new Object[] { false, (byte) 11, 'z', (short) 22, 33, 44L, 55.55f, 66.66d });
    }

    @Test
    public void testGetParametersShouldCorrectlyTrimNonSpaceWhitespaceChars() {
        // Given:
        String data = "\n-1f\n,\r-2\r,\t3.0d\t";

        Class<?>[] parameterTypes = new Class[] { float.class, int.class, double.class };
        int rowIdx = 4;

        // When:
        Object[] result = underTest.getParameters(data, parameterTypes, rowIdx);

        // Then:
        assertThat(result).isEqualTo(new Object[] { -1f, -2, 3d });
    }

    @Test
    public void testGetParametersShouldNotTrimNonBreakingSpace() {
        // Given:
        String data = "\u00A0test\u00A0";

        Class<?>[] parameterTypes = new Class[] { String.class };
        int rowIdx = 5;

        // When:
        Object[] result = underTest.getParameters(data, parameterTypes, rowIdx);

        // Then:
        assertThat(result).isEqualTo(new Object[] { "\u00A0test\u00A0" });
    }

    @Test
    public void testGetParametersShouldCorrectlyHandleLeadingEmptyString() {
        // Given:
        String data = ",true";
        Class<?>[] parameterTypes = new Class[] { String.class, boolean.class };
        int rowIdx = 6;

        // When:
        Object[] result = underTest.getParameters(data, parameterTypes, rowIdx);

        // Then:
        assertThat(result).isEqualTo(new Object[] { "", true });
    }

    @Test
    public void testGetParametersShouldCorrectlyHandleTrailingEmptyString() {
        // Given:
        String data = "1,";
        Class<?>[] parameterTypes = new Class[] { int.class, String.class };
        int rowIdx = 7;

        // When:
        Object[] result = underTest.getParameters(data, parameterTypes, rowIdx);

        // Then:
        assertThat(result).isEqualTo(new Object[] { 1, "" });
    }

    @Test(expected = Error.class)
    public void testGetParametersShouldThrowErrorIfCharHasNotLengthOne() {
        // Given:
        String data = "noChar";
        Class<?>[] parameterTypes = new Class[] { char.class };
        int rowIdx = 8;

        // When:
        underTest.getParameters(data, parameterTypes, rowIdx);

        // Then: expect exception
    }

    @Test(expected = Error.class)
    public void testGetParametersShouldThrowErrorForUnsupportedTargetType() {
        // Given:
        String data = "noObject";
        Class<?>[] parameterTypes = new Class[] { Object.class };
        int rowIdx = 9;

        // When:
        underTest.getParameters(data, parameterTypes, rowIdx);

        // Then: expect exception
    }

    @Test
    public void testGetParametersShouldCorrectlyParseEnum() {
        // Given:
        String data = " VAL1,  VAL2 ";
        Class<?>[] parameterTypes = new Class[] { TestEnum.class, TestEnum.class };
        int rowIdx = 10;

        // When:
        Object[] result = underTest.getParameters(data, parameterTypes, rowIdx);

        // Then:
        assertThat(result).isEqualTo(new Object[] { TestEnum.VAL1, TestEnum.VAL2 });
    }

    @Test(expected = Error.class)
    public void testGetParametersShouldThrowErrorIfEnumValueIsInvalid() {
        // Given:
        String data = "UNKNOW_ENUM_VALUE";
        Class<?>[] parameterTypes = new Class[] { TestEnum.class };
        int rowIdx = 11;

        // When:
        underTest.getParameters(data, parameterTypes, rowIdx);

        // Then: expect exception
    }

    @Test
    public void testGetParametersShouldCorrectlyParseAllPrimitiveWrapperTypes() {
        // Given:
        String data = "true,1,c,2,3,4,5.5,6.6";
        Class<?>[] parameterTypes = new Class[] { Boolean.class, Byte.class, Character.class, Short.class,
                Integer.class, Long.class, Float.class, Double.class };
        int rowIdx = 12;

        // When:
        Object[] result = underTest.getParameters(data, parameterTypes, rowIdx);

        // Then:
        assertThat(result).isEqualTo(
                new Object[] { Boolean.TRUE, Byte.valueOf((byte) 1), Character.valueOf('c'), Short.valueOf((short) 2),
                        Integer.valueOf(3), Long.valueOf(4L), Float.valueOf(5.5f), Double.valueOf(6.6d) });
    }

    @Test
    public void testGetParametersShouldCorrectlyParseNullValue() {
        // Given:
        String data = "null, null  ";
        Class<?>[] parameterTypes = new Class[] { Boolean.class, String.class };
        int rowIdx = 13;

        // When:
        Object[] result = underTest.getParameters(data, parameterTypes, rowIdx);

        // Then:
        assertThat(result).isEqualTo(new Object[] { null, null });
    }

    // -- helper methods -----------------------------------------------------------------------------------------------

    // Methods used to test isValidDataProviderMethod
    static Object[][] nonPublicDataProviderMethod() {
        return null;
    }

    public List<List<Object>> nonStaticDataProviderMethod() {
        return null;
    }

    public static Object[][] nonNoArgDataProviderMethod(@SuppressWarnings("unused") Object obj) {
        return null;
    }

    public static String stringReturningDataProviderMethod() {
        return null;
    }

    public static List<Object> listReturningDataProviderMethod() {
        return null;
    }

    public static List<Iterable<Object>> listOfIterableReturningDataProviderMethod() {
        return null;
    }

    public static Set<Set<Object>> iterableOfIterableReturningDataProviderMethod() {
        return null;
    }

    public static Set<Set<Object>> setOfSetReturningDataProviderMethod() {
        return null;
    }

    @SuppressWarnings("serial")
    private static class TwoArgList<A, B> extends ArrayList<A> {
        // not required for now :-)
    }

    public static TwoArgList<List<Object>, List<Object>> twoArgListReturningDataProviderMethod() {
        return null;
    }

    Object[][] nonPublicNonStaticNonNoArgDataProviderMethod(String arg1) {
        return new Object[][] { { arg1 } };
    }

    public static Object[][] validDataProviderMethodArray() {
        return null;
    }

    public static List<List<Object>> validDataProviderMethodList() {
        return null;
    }

    @SuppressWarnings("unused")
    public void testStringString(String s1, String s2) {
        // Method to test @DataProvider simple row parsing
    }

    @SuppressWarnings("serial")
    private static class SubList<A> extends ArrayList<A> {
        // not required for now :-)
    }

    public static SubList<SubList<Object>> validDataProviderMethodSubList() {
        return null;
    }
}
