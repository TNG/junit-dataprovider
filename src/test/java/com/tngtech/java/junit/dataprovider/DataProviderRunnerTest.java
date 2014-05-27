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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

import com.tngtech.java.junit.dataprovider.internal.DataConverter;
import com.tngtech.test.java.junit.dataprovider.category.CategoryOne;

public class DataProviderRunnerTest extends BaseTest {

    @Spy
    private DataProviderRunner underTest;

    @Mock
    private DataConverter dataConverter;
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
        FrameworkMethod testMethod2 = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod2 = mock(FrameworkMethod.class);

        doReturn(asList(testMethod, testMethod2)).when(testClass).getAnnotatedMethods(UseDataProvider.class);
        doReturn(dataProviderMethod).when(underTest).getDataProviderMethod(testMethod);
        doReturn(dataProviderMethod2).when(underTest).getDataProviderMethod(testMethod2);
        doReturn(mock(UseDataProvider.class)).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(mock(UseDataProvider.class)).when(testMethod2).getAnnotation(UseDataProvider.class);

        doNothing().when(underTest).validateDataProviderMethod(any(FrameworkMethod.class), anyListOf(Throwable.class));

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateDataProviderMethods(errors);

        // Then:
        verify(underTest).validateDataProviderMethod(dataProviderMethod, errors);
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
        doReturn(null).when(underTest).getDataProviderMethod(testMethod);

        // When:
        List<FrameworkMethod> result = underTest.generateExplodedTestMethodsFor(asList(testMethod));

        // Then:
        assertThat(result).containsOnly(testMethod);
    }

    @Test
    public void testGenerateExplodedTestMethodsForShouldReturnExplodedTestMethodsForValidDataProvider() {
        // Given:
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

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfItIsNotPublic() {
        String dataProviderName = "dataProviderNotPublic";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("nonPublicDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(true).when(dataConverter).canConvert(any(Type.class));

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

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("nonStaticDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(true).when(dataConverter).canConvert(any(Type.class));

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

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("nonNoArgDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(true).when(dataConverter).canConvert(any(Type.class));

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must have no parameters");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfNonConvertableReturnType() {
        // Given:
        String dataProviderName = "dataProviderNonConvertableReturnType";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("validDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(false).when(dataConverter).canConvert(any(Type.class));

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either return Object[][] or List<List<Object>>");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorsIfItIsNotPublicAndNotStaticAndNonNoArgAndNonConvertableReturnType() {
        // Given:
        String dataProviderName = "dataProviderNotPublicNotStaticNonNoArg";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("nonPublicNonStaticNonNoArgDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(false).when(dataConverter).canConvert(any(Type.class));

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).hasSize(4);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase("must be public");
        assertThat(errors.get(1).getMessage()).contains(dataProviderName).containsIgnoringCase("must be static");
        assertThat(errors.get(2).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must have no parameters");
        assertThat(errors.get(3).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either return Object[][] or List<List<Object>>");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddNoErrorIfItIsValid() {
        // Given:
        String dataProviderName = "validDataProvider";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("validDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(true).when(dataConverter).canConvert(any(Type.class));

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, errors);

        // Then:
        assertThat(errors).isEmpty();
    }

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
        List<List<Object>> dataProviderMethodResult = list(this.<Object> list(10L, 11, "12"));
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
                new Object[] { 44, "55",
                66L }, new Object[] { 77, "88", 99L });
        doReturn(dataConverterResult).when(dataConverter).convert(any(), any(Class[].class));
        List<List<?>> dataProviderMethodResult = list(this.<Object> list(1, "a"), list(3, "b"), list(5, "c"));

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

    @Test
    public void testIsFilterBlackListedShouldReturnFalseForJunitPackagedFilter() {
        // Given:
        Filter filter = Filter.ALL;

        // When:
        boolean result = underTest.isFilterBlackListed(filter);

        // Then:
        assertThat(result).isFalse();
    }

    // -- helper methods -----------------------------------------------------------------------------------------------

    // Methods used to test isValidDataProviderMethod
    static Object[][] nonPublicDataProviderMethod() {
        return null;
    }

    public List<List<Object>> nonStaticDataProviderMethod() {
        return null;
    }

    public static Object[][] nonNoArgDataProviderMethod(Object obj) {
        return new Object[][] { { obj } };
    }

    String nonPublicNonStaticNonNoArgDataProviderMethod(String arg1) {
        return arg1;
    }

    public static Object[][] validDataProviderMethod() {
        return null;
    }

    private void assertDataProviderFrameworkMethods(List<FrameworkMethod> actuals, List<Object[]> expecteds) {
        assertThat(actuals).hasSameSizeAs(expecteds);
        for (int idx = 0; idx < actuals.size(); idx++) {
            assertThat(actuals.get(idx)).describedAs("at index " + idx).isInstanceOf(DataProviderFrameworkMethod.class);

            DataProviderFrameworkMethod actual = (DataProviderFrameworkMethod) actuals.get(idx);
            assertThat(actual.idx).describedAs("at index " + idx).isEqualTo(idx);
            assertThat(actual.parameters).describedAs("at index " + idx).isEqualTo(expecteds.get(idx));
        }
    }
}
