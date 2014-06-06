package com.tngtech.java.junit.dataprovider.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

public class TestValidator {

    private final DataConverter dataConverter;

    public TestValidator(DataConverter dataConverter) {
        if (dataConverter == null) {
            throw new NullPointerException("dataConverter must not be null");
        }
        this.dataConverter = dataConverter;
    }

    /**
     * Checks if the given {@code testMethod} is a valid test method depending on the data provider relevant annotation
     * {@code @}{@link DataProvider} and {@code @}{@link UseDataProvider}. Adds {@link Exception}s to {@code errors} for
     * each invalid property. A normal test method must be is public, void instance method with no arguments. A data
     * provider test method must be is public and void instance method but have a least one argument.
     *
     * @param testMethod the test method to be validated
     * @param errors
     * @throws IllegalArgumentException if given {@code errors} is {@code null}
     */
    public void validateTestMethod(FrameworkMethod testMethod, List<Throwable> errors) {
        if (testMethod == null) {
            throw new NullPointerException("testMethod must not be null");
        }
        if (errors == null) {
            throw new NullPointerException("errors must not be null");
        }

        UseDataProvider useDataProvider = testMethod.getAnnotation(UseDataProvider.class);
        DataProvider dataProvider = testMethod.getAnnotation(DataProvider.class);

        if (useDataProvider != null && dataProvider != null) {
            errors.add(new Exception(String.format("Method %s() should either have @%s or @%s annotation", testMethod
                    .getName(), useDataProvider.getClass().getSimpleName(), dataProvider.getClass().getSimpleName())));

        } else if (useDataProvider == null && dataProvider == null) {
            testMethod.validatePublicVoidNoArg(false, errors);

        } else {
            testMethod.validatePublicVoid(false, errors);
            if (testMethod.getMethod().getParameterTypes().length <= 0) {
                errors.add(new Exception(String.format("Method %s() must have at least one argument for data provider",
                        testMethod.getName())));
            }
        }
    }

    /**
     * Checks if the given {@code dataProviderMethod} is a valid data provider and adds a {@link Exception} to
     * {@code errors} if it
     * <ul>
     * <li>is not public,</li>
     * <li>is not static,</li>
     * <li>takes parameters, or</li>
     * <li>does return a convertible type, see {@link DataConverter#canConvert(java.lang.reflect.Type)}
     * </ul>
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     * <ul>
     *
     * @param dataProviderMethod the method to check
     * @param dataProvider the {@code @}{@link DataProvider} annotation used on {@code dataProviderMethod}
     * @param errors to be "returned" and thrown as {@link InitializationError}
     * @throws NullPointerException iif any argument is {@code null}
     */
    public void validateDataProviderMethod(FrameworkMethod dataProviderMethod, DataProvider dataProvider,
            List<Throwable> errors) {

        if (dataProviderMethod == null) {
            throw new NullPointerException("dataProviderMethod must not be null");
        }
        if (dataProvider == null) {
            throw new NullPointerException("dataProvider must not be null");
        }
        if (errors == null) {
            throw new NullPointerException("errors must not be null");
        }

        Method method = dataProviderMethod.getMethod();

        String messageBasePart = "Data provider method '" + dataProviderMethod.getName() + "' must";
        if (!Modifier.isPublic(method.getModifiers())) {
            errors.add(new Exception(messageBasePart + " be public"));
        }
        if (!Modifier.isStatic(method.getModifiers())) {
            errors.add(new Exception(messageBasePart + " be static"));
        }
        if (method.getParameterTypes().length != 0) {
            errors.add(new Exception(messageBasePart + " have no parameters"));
        }
        if (!dataConverter.canConvert(method.getGenericReturnType())) {
            errors.add(new Exception(messageBasePart + " either return Object[][] or List<List<Object>>"));
        }
        if (dataProvider.value().length > 0) {
            errors.add(new Exception(messageBasePart + " not define @DataProvider.value()"));
        }
    }
}
