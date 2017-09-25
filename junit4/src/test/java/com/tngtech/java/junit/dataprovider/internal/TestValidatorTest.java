package com.tngtech.java.junit.dataprovider.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@RunWith(MockitoJUnitRunner.class)
public class TestValidatorTest extends BaseTest {

    @InjectMocks
    private TestValidator underTest;

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

    @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = NullPointerException.class)
    public void testTestValidatorShouldThrowNullPointerExceptionIfDataConverterIsNull() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        TestValidator result = new TestValidator(null);

        // Then: expect exception
    }

    @Test(expected = NullPointerException.class)
    public void testValidateTestMethodShouldThrowNullPointerExceptionIfTestMethodIsNull() {
        // Given:
        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethod(null, errors);

        // Then: expect exception
    }

    @Test(expected = NullPointerException.class)
    public void testValidateTestMethodShouldThrowNullPointerExceptionIfErrorsIsNull() {
        // Given:

        // When:
        underTest.validateTestMethod(testMethod, null);

        // Then: expect exception
    }

    @Test
    public void testValidateTestMethodShouldAddErrorIfDataProviderAndUseDataProviderTestMethod() {
        // Given:
        doReturn("test1").when(testMethod).getName();

        doReturn(dataProvider).when(testMethod).getAnnotation(DataProvider.class);
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethod(testMethod, errors);

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

    @Test
    public void testValidateTestMethodShouldCheckForPublicVoidNoArgIfNormalTestMethod() {
        // Given:
        doReturn(null).when(testMethod).getAnnotation(DataProvider.class);
        doReturn(null).when(testMethod).getAnnotation(UseDataProvider.class);

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethod(testMethod, errors);

        // Then:
        assertThat(errors).isEmpty();

        verify(testMethod).getAnnotation(DataProvider.class);
        verify(testMethod).getAnnotation(UseDataProvider.class);
        verify(testMethod).validatePublicVoidNoArg(false, errors);
        verifyNoMoreInteractions(testMethod);
    }

    @Test
    public void testValidateTestMethodShouldCheckForPublicVoidIfDataProviderTestMethod() {
        // Given:
        doReturn(dataProvider).when(testMethod).getAnnotation(DataProvider.class);
        doReturn(null).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(getMethod("testOneArg")).when(testMethod).getMethod();

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethod(testMethod, errors);

        // Then:
        assertThat(errors).isEmpty();

        verify(testMethod).getAnnotation(DataProvider.class);
        verify(testMethod).getAnnotation(UseDataProvider.class);
        verify(testMethod).validatePublicVoid(false, errors);
        verify(testMethod).getMethod();
        verifyNoMoreInteractions(testMethod);
    }

    @Test
    public void testValidateTestMethodShouldCheckForPublicVoidIfUseDataProviderTestMethod() {
        // Given:
        doReturn(null).when(testMethod).getAnnotation(DataProvider.class);
        doReturn(useDataProvider).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(getMethod("testOneArg")).when(testMethod).getMethod();

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethod(testMethod, errors);

        // Then:
        assertThat(errors).isEmpty();

        verify(testMethod).getAnnotation(DataProvider.class);
        verify(testMethod).getAnnotation(UseDataProvider.class);
        verify(testMethod).validatePublicVoid(false, errors);
        verify(testMethod).getMethod();
        verifyNoMoreInteractions(testMethod);
    }

    @Test
    public void testValidateTestMethodShouldAddErrorForNoArgTestMethodIfUseDataProviderTestMethodWithNoArgs() {
        // Given:
        doReturn(dataProvider).when(testMethod).getAnnotation(DataProvider.class);
        doReturn(null).when(testMethod).getAnnotation(UseDataProvider.class);
        doReturn(getMethod("testNoArg")).when(testMethod).getMethod();
        doReturn("testNoArg").when(testMethod).getName();

        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateTestMethod(testMethod, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).isInstanceOf(Exception.class);
        assertThat(errors.get(0).getMessage()).matches(
                "Method testNoArg\\(\\) must have at least one argument for dataprovider");

        verify(testMethod).getAnnotation(DataProvider.class);
        verify(testMethod).getAnnotation(UseDataProvider.class);
        verify(testMethod).validatePublicVoid(false, errors);
        verify(testMethod).getMethod();
        verify(testMethod).getName();
        verifyNoMoreInteractions(testMethod);
    }

    @Test(expected = NullPointerException.class)
    public void testValidateDataProviderMethodShouldThrowNullPointerExceptionIfUseDataProviderMethodIsNull() {
        // Given:
        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateDataProviderMethod(null, dataProvider, errors);

        // Then: expect exception
    }

    @Test(expected = NullPointerException.class)
    public void testValidateDataProviderMethodShouldThrowNullPointerExceptionIfDataProviderIsNull() {
        // Given:
        List<Throwable> errors = new ArrayList<Throwable>();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, null, errors);

        // Then: expect exception
    }

    @Test(expected = NullPointerException.class)
    public void testValidateDataProviderMethodShouldThrowNullPointerExceptionIfErrorsIsNull() {
        // Given:

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, dataProvider, null);

        // Then: expect exception
    }

    @Test
    public void testValidateDataProviderMethodShouldAddNoErrorIfDataProviderMethodIsValid() {
        // Given:
        String dataProviderName = "validDataProvider";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("validDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(true).when(dataConverter).canConvert(any(Type.class));
        doReturn(new String[0]).when(dataProvider).value();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, dataProvider, errors);

        // Then:
        assertThat(errors).isEmpty();
    }

    @Test
    public void testValidateDataProviderMethodShouldAddNoErrorIfDataProviderMethodWithFrameworkMethodParameterIsValid() {
        // Given:
        String dataProviderName = "validDataProvider";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("validDataProviderMethodWithFrameworkMethodParameter")).when(dataProviderMethod).getMethod();
        doReturn(true).when(dataConverter).canConvert(any(Type.class));
        doReturn(new String[0]).when(dataProvider).value();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, dataProvider, errors);

        // Then:
        assertThat(errors).isEmpty();
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfDataProviderMethodIsNotPublic() {
        // Given:
        String dataProviderName = "dataProviderNotPublic";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("nonPublicDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(true).when(dataConverter).canConvert(any(Type.class));
        doReturn(new String[0]).when(dataProvider).value();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, dataProvider, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase("must be public");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfDataProviderMethodIsNotStatic() {
        // Given:
        String dataProviderName = "dataProviderNotStatic";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("nonStaticDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(true).when(dataConverter).canConvert(any(Type.class));
        doReturn(new String[0]).when(dataProvider).value();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, dataProvider, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase("must be static");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfDataProviderMethodRequiresOneParametersOfInvalidType() {
        // Given:
        String dataProviderName = "dataProviderNonNoArg";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("wrongArgDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(true).when(dataConverter).canConvert(any(Type.class));
        doReturn(new String[0]).when(dataProvider).value();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, dataProvider, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either have a single FrameworkMethod parameter or none");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfDataProviderMethodRequiresTwoParameters() {
        // Given:
        String dataProviderName = "dataProviderNonNoArg";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("twoArgDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(true).when(dataConverter).canConvert(any(Type.class));
        doReturn(new String[0]).when(dataProvider).value();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, dataProvider, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either have a single FrameworkMethod parameter or none");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfDataProviderMethodReturnsNonConvertableType() {
        // Given:
        String dataProviderName = "dataProviderNonConvertableReturnType";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("validDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(false).when(dataConverter).canConvert(any(Type.class));
        doReturn(new String[0]).when(dataProvider).value();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, dataProvider, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either return Object[][], Object[], String[], Iterable<Iterable<?>>, or Iterable<?>");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorIfDataProviderDefinesValue() {
        // Given:
        String dataProviderName = "dataProviderNonConvertableReturnType";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("validDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(true).when(dataConverter).canConvert(any(Type.class));
        doReturn(new String[] { "test" }).when(dataProvider).value();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, dataProvider, errors);

        // Then:
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must not define @DataProvider.value()");
    }

    @Test
    public void testValidateDataProviderMethodShouldAddErrorsIfDataProviderMethodIsCompletelyWrong() {
        // Given:
        String dataProviderName = "dataProviderNotPublicNotStaticNonNoArg";

        List<Throwable> errors = new ArrayList<Throwable>();

        doReturn(dataProviderName).when(dataProviderMethod).getName();
        doReturn(getMethod("nonPublicNonStaticNonNoArgDataProviderMethod")).when(dataProviderMethod).getMethod();
        doReturn(false).when(dataConverter).canConvert(any(Type.class));
        doReturn(new String[] { "test" }).when(dataProvider).value();

        // When:
        underTest.validateDataProviderMethod(dataProviderMethod, dataProvider, errors);

        // Then:
        assertThat(errors).hasSize(5);
        assertThat(errors.get(0).getMessage()).contains(dataProviderName).containsIgnoringCase("must be public");
        assertThat(errors.get(1).getMessage()).contains(dataProviderName).containsIgnoringCase("must be static");
        assertThat(errors.get(2).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either have a single FrameworkMethod parameter or none");
        assertThat(errors.get(3).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must either return Object[][], Object[], String[], Iterable<Iterable<?>>, or Iterable<?>");
        assertThat(errors.get(4).getMessage()).contains(dataProviderName).containsIgnoringCase(
                "must not define @DataProvider.value()");
    }

    // -- helper methods -----------------------------------------------------------------------------------------------

    // Methods used to test isValidDataProviderMethod
    public void testNoArg() {
        return;
    }

    public void testOneArg(@SuppressWarnings("unused") String arg) {
        return;
    }

    static Object[][] nonPublicDataProviderMethod() {
        return null;
    }

    public List<List<Object>> nonStaticDataProviderMethod() {
        return null;
    }

    public static Object[][] wrongArgDataProviderMethod(Object obj) {
        return new Object[][] { { obj } };
    }

    public static Object[][] twoArgDataProviderMethod(FrameworkMethod testMethod, Object obj) {
        return new Object[][] { { testMethod, obj } };
    }

    String nonPublicNonStaticNonNoArgDataProviderMethod(String arg1) {
        return arg1;
    }

    public static Object[][] validDataProviderMethod() {
        return null;
    }

    public static Object[][] validDataProviderMethodWithFrameworkMethodParameter(
            @SuppressWarnings("unused") FrameworkMethod testMethod) {
        return null;
    }
}
