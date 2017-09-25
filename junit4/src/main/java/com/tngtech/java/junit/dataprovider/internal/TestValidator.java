package com.tngtech.java.junit.dataprovider.internal;

import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkNotNull;

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
        this.dataConverter = checkNotNull(dataConverter, "dataConverter must not be null");
    }

    /**
     * Checks if the given {@code testMethod} is a valid test method depending on the dataprovider relevant annotation
     * {@code @}{@link DataProvider} and {@code @}{@link UseDataProvider}. Adds {@link Exception}s to {@code errors} for
     * each invalid property. A normal test method must be is public, void instance method with no arguments. A data
     * provider test method must be is public and void instance method but have a least one argument.
     *
     * @param testMethod the test method to be validated
     * @param errors to be "returned" and thrown as {@link InitializationError}
     * @throws IllegalArgumentException if given {@code errors} is {@code null}
     */
    public void validateTestMethod(FrameworkMethod testMethod, List<Throwable> errors) {
        checkNotNull(testMethod, "testMethod must not be null");
        checkNotNull(errors, "errors must not be null");

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
                errors.add(new Exception(String.format("Method %s() must have at least one argument for dataprovider",
                        testMethod.getName())));
            }
        }
    }

    /**
     * Checks if the given {@code dataProviderMethod} is a valid dataprovider and adds a {@link Exception} to
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
     *
     * @param dataProviderMethod the method to check
     * @param dataProvider the {@code @}{@link DataProvider} annotation used on {@code dataProviderMethod}
     * @param errors to be "returned" and thrown as {@link InitializationError}
     * @throws NullPointerException iif any argument is {@code null}
     */
    public void validateDataProviderMethod(FrameworkMethod dataProviderMethod, DataProvider dataProvider,
            List<Throwable> errors) {

        checkNotNull(dataProviderMethod, "dataProviderMethod must not be null");
        checkNotNull(dataProvider, "dataProvider must not be null");
        checkNotNull(errors, "errors must not be null");

        Method method = dataProviderMethod.getMethod();

        String messageBasePart = "Dataprovider method '" + dataProviderMethod.getName() + "' must";
        if (!Modifier.isPublic(method.getModifiers())) {
            errors.add(new Exception(messageBasePart + " be public"));
        }
        if (!Modifier.isStatic(method.getModifiers())) {
            errors.add(new Exception(messageBasePart + " be static"));
        }
        if (method.getParameterTypes().length != 0
                && (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0]
                        .equals(FrameworkMethod.class))) {
            errors.add(new Exception(messageBasePart + " either have a single FrameworkMethod parameter or none"));
        }
        if (!dataConverter.canConvert(method.getGenericReturnType())) {
            errors.add(new Exception(messageBasePart
                    + " either return Object[][], Object[], String[], Iterable<Iterable<?>>, or Iterable<?>, whereby any subtype of Iterable as well as an arbitrary inner type are also accepted"));
        }
        if (dataProvider.value().length > 0) {
            errors.add(new Exception(messageBasePart + " not define @DataProvider.value()"));
        }
    }
}
