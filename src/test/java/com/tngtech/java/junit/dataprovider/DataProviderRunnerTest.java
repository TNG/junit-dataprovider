package com.tngtech.java.junit.dataprovider;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class DataProviderRunnerTest {

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
        verify(testMethod).getAnnotation(UseDataProvider.class);
        verify(testMethod).validatePublicVoidNoArg(false, errors);
        verifyNoMoreInteractions(testMethod);
    }

    @Test
    public void testValidateTestMethodsShouldCheckForPublicVoidIfDataProviderTestMethod() {

        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        UseDataProvider useDataProvider = mock(UseDataProvider.class);

        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(Test.class);
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethods(errors);

        // Then:
        verify(testMethod).getAnnotation(UseDataProvider.class);
        verify(testMethod).validatePublicVoid(false, errors);
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

        final String dataProviderName = "dataProviderMethodName";

        // Given:
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
    public void testValidateDataProviderMethodsShouldAddErrorIfDataProviderIsNotValid() {

        final String dataProviderName = "dataProviderMethodName";

        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        UseDataProvider useDataProvider = mock(UseDataProvider.class);

        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(UseDataProvider.class);
        doReturn(dataProviderMethod).when(underTest).getDataProviderMethod(testMethod);
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(dataProviderName).when(useDataProvider).value();
        doReturn(false).when(underTest).isValidDataProviderMethod(dataProviderMethod);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateDataProviderMethods(errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must be public, static, has no arguments parameters and returns 'Object[][]'");
    }

    @Test
    public void testValidateDataProviderMethodsShouldNotAddAnyErrorIfDataProviderIsValid() {

        final String dataProviderName = "dataProviderMethodName";

        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);
        UseDataProvider useDataProvider = mock(UseDataProvider.class);

        doReturn(asList(testMethod)).when(testClass).getAnnotatedMethods(UseDataProvider.class);
        doReturn(dataProviderMethod).when(underTest).getDataProviderMethod(testMethod);
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(dataProviderName).when(useDataProvider).value();
        doReturn(true).when(underTest).isValidDataProviderMethod(dataProviderMethod);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateDataProviderMethods(errors);

        // Then:
        assertThat(errors).isEmpty();
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
    public void testGenerateExplodedTestMethodsForShouldReturnOriginalTestMethodIfDataProviderMethodIsInvalid() {

        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        doReturn(dataProviderMethod).when(underTest).getDataProviderMethod(testMethod);
        doReturn(false).when(underTest).isValidDataProviderMethod(dataProviderMethod);

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
        doReturn(true).when(underTest).isValidDataProviderMethod(dataProviderMethod);

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

        // When:
        FrameworkMethod result = underTest.getDataProviderMethod(testMethod);

        // Then:
        assertThat(result).isNull();
    }

    @Test
    public void testGetDataProviderMethodShouldReturnDataProviderMethodIfItExists() {

        final String dataProviderMethodName = "availableDataProviderMethod";

        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        UseDataProvider useDataProvider = mock(UseDataProvider.class);

        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(dataProviderMethodName).when(useDataProvider).value();

        doReturn(asList(dataProviderMethod)).when(testClass).getAnnotatedMethods(DataProvider.class);
        doReturn(dataProviderMethodName).when(dataProviderMethod).getName();

        // When:
        FrameworkMethod result = underTest.getDataProviderMethod(testMethod);

        // Then:
        assertThat(result).isEqualTo(dataProviderMethod);
    }

    @Test
    public void testIsValidDataProviderMethodShouldReturnFalseIfDataProviderMethodIsNull() {

        // Given:

        // When:
        boolean result = underTest.isValidDataProviderMethod(null);

        // Then:
        assertThat(result).isFalse();
    }
    @Test
    public void testIsValidDataProviderMethodShouldReturnFalseIfItIsNotPublic() {

        // Given:
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        doReturn(getMethod("nonPublicDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        boolean result = underTest.isValidDataProviderMethod(dataProviderMethod);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testIsValidDataProviderMethodShouldReturnFalseIfItIsNotStatic() {

        // Given:
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        doReturn(getMethod("nonStaticDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        boolean result = underTest.isValidDataProviderMethod(dataProviderMethod);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testIsValidDataProviderMethodShouldReturnFalseIfItRequiresAnyParameter() {

        // Given:
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        doReturn(getMethod("nonNoArgDataProviderMethod", Object.class)).when(dataProviderMethod).getMethod();

        // When:
        boolean result = underTest.isValidDataProviderMethod(dataProviderMethod);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testIsValidDataProviderMethodShouldReturnFalseIfItDoesNotReturnObjectArrayArray() {

        // Given:
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        doReturn(getMethod("nonObjectArrayArrayReturningDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        boolean result = underTest.isValidDataProviderMethod(dataProviderMethod);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testIsValidDataProviderMethodShouldReturnTrueIfItIsPublicStaticNoArgAndReturnsObjectArrayArray() {

        // Given:
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        doReturn(getMethod("validDataProviderMethod")).when(dataProviderMethod).getMethod();

        // When:
        boolean result = underTest.isValidDataProviderMethod(dataProviderMethod);

        // Then:
        assertThat(result).isTrue();
    }

    @Test(expected = Error.class)
    public void testExplodeTestMethodsShouldThrowErrorIfDataProviderMethodThrowsException() throws Throwable {

        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        doThrow(NullPointerException.class).when(dataProviderMethod).invokeExplosively(null);

        // When:
        underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then: expect exception
    }

    @Test(expected = Error.class)
    public void testExplodeTestMethodsShouldThrowErrorIfDataProviderMethodReturnsNull() throws Throwable {

        // Given:
        FrameworkMethod testMethod = mock(FrameworkMethod.class);
        FrameworkMethod dataProviderMethod = mock(FrameworkMethod.class);

        doReturn(null).when(dataProviderMethod).invokeExplosively(null);

        // When:
        underTest.explodeTestMethod(testMethod, dataProviderMethod);

        // Then: expect exception
    }

    @Test(expected = Error.class)
    public void testExplodeTestMethodsShouldThrowErrorIfDataProviderMethodReturnsEmptyObjectArrayArray()
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
    public void testExplodeTestMethodsShouldReturnOneDataProviderFrameworkMethodIfDataProviderMethodReturnsOneRow()
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
    public void testExplodeTestMethodsShouldReturnMultipleDataProviderFrameworkMethodIfDataProviderMethodReturnsMultipleRow()
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

    private Method getMethod(String methodName, Class<?>... args) {
        final Class<? extends DataProviderRunnerTest> clazz = this.getClass();
        try {
            return clazz.getDeclaredMethod(methodName, args);
        } catch (Exception e) {
            fail(String.format("No method with name '%s' found in %s", methodName, clazz));
            return null; // fool compiler
        }
    }

    // Methods used to test isValidDataProviderMethod
    static Object[][] nonPublicDataProviderMethod() {
        return null;
    }

    public Object[][] nonStaticDataProviderMethod() {
        return null;
    }

    public static Object[][] nonNoArgDataProviderMethod(@SuppressWarnings("unused") Object obj) {
        return null;
    }

    public static String nonObjectArrayArrayReturningDataProviderMethod() {
        return null;
    }

    public static Object[][] validDataProviderMethod() {
        return null;
    }

}
