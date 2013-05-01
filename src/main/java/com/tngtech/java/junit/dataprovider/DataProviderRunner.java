package com.tngtech.java.junit.dataprovider;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

/**
 * A custom runner for JUnit that allows the usage of <a href="http://testng.org/">TestNG</a>-like data providers. Data
 * providers are public, static methods that return an {@link Object}{@code [][]} (see {@link DataProvider}).
 * <p>
 * Your test method must be annotated with {@code @}{@link UseDataProvider}, additionally.
 */
public class DataProviderRunner extends BlockJUnit4ClassRunner {

    /**
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    List<FrameworkMethod> computedTestMethods;

    /**
     * Creates a DataProviderRunner to run supplied {@code clazz}.
     *
     * @param clazz the test {@link Class} to run
     * @throws InitializationError if the test {@link Class} is malformed.
     */
    public DataProviderRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    public void filter(final Filter filter) throws NoTestsRemainException {
        super.filter(new DataProviderFilter(filter));
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        if (computedTestMethods == null) {
            computedTestMethods = generateExplodedTestMethodsFor(super.computeTestMethods());
        }
        return computedTestMethods;
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        super.collectInitializationErrors(errors);
        validateDataProviderMethods(errors);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if given {@code errors} is {@code null}
     */
    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        if (errors == null) {
            throw new IllegalArgumentException("errors must not be null");
        }
        for (FrameworkMethod method : getTestClassInt().getAnnotatedMethods(Test.class)) {
            if (method.getAnnotation(UseDataProvider.class) == null) {
                method.validatePublicVoidNoArg(false, errors);
            } else {
                method.validatePublicVoid(false, errors);
            }
        }
    }

    /**
     * Validates test methods and their data providers. This method cannot use the result of
     * {@link DataProviderRunner#computeTestMethods()} because the method ignores invalid test methods and data
     * providers silently (except if a data provider method cannot be called). However, the common errors are not raised
     * as {@link RuntimeException} to go the JUnit way of detecting errors. This implies that we have to browse the
     * whole class for test methods and data providers again :-(.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param errors that are added to this list
     * @throws IllegalArgumentException if given {@code errors} is {@code null}
     */
    void validateDataProviderMethods(List<Throwable> errors) {
        if (errors == null) {
            throw new IllegalArgumentException("errors must not be null");
        }
        for (FrameworkMethod testMethod : getTestClassInt().getAnnotatedMethods(UseDataProvider.class)) {
            String dataProviderName = testMethod.getAnnotation(UseDataProvider.class).value();

            FrameworkMethod dataProviderMethod = getDataProviderMethod(testMethod);
            if (dataProviderMethod == null) {
                errors.add(new Error("No such data provider: " + dataProviderName));
            } else if (!isValidDataProviderMethod(dataProviderMethod)) {
                errors.add(new Error("The data provider method '" + dataProviderName + "' is not valid. "
                        + "A valid method must be public, static, has no arguments parameters and returns 'Object[][]'"));
            }
        }
    }

    /**
     * Generates the exploded list of test methods for the given {@code testMethods}. Each of the given
     * {@link FrameworkMethod}s is checked if it uses a {@code @}{@link DataProvider} or not. If yes, for each line of
     * the {@link DataProvider}s {@link Object}{@code [][]} result a specific test method with its parameters (=
     * {@link Object}{@code []} will be added. If no, the original test method is added.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param testMethods the original test methods
     * @return the exploded list of test methods (never {@code null})
     */
    List<FrameworkMethod> generateExplodedTestMethodsFor(List<FrameworkMethod> testMethods) {
        List<FrameworkMethod> result = new ArrayList<FrameworkMethod>();
        if (testMethods == null) {
            return result;
        }
        for (FrameworkMethod testMethod : testMethods) {
            FrameworkMethod dataProviderMethod = getDataProviderMethod(testMethod);

            if (isValidDataProviderMethod(dataProviderMethod)) {
                result.addAll(explodeTestMethod(testMethod, dataProviderMethod));
            } else {
                result.add(testMethod);
            }
        }
        return result;
    }

    /**
     * Returns the data provider method that belongs to the given test method or {@code null} if no such data provider
     * exists or the test method is not marked for usage of a data provider
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param testMethod test method that uses a data provider
     * @return the data provider or {@code null} (if data provider does not exist or test method does not use any)
     * @throws IllegalArgumentException if given {@code testMethod} is {@code null}
     */
    FrameworkMethod getDataProviderMethod(FrameworkMethod testMethod) {
        if (testMethod == null) {
            throw new IllegalArgumentException("testMethod must not be null");
        }
        UseDataProvider testUsingDataProvider = testMethod.getAnnotation(UseDataProvider.class);
        if (testUsingDataProvider == null) {
            return null;
        }
        for (FrameworkMethod method : getTestClassInt().getAnnotatedMethods(DataProvider.class)) {
            if (method.getName().equals(testUsingDataProvider.value())) {
                return method;
            }
        }
        return null;
    }

    /**
     * Checks if the given method is a valid data provider. A method is a valid data provider if and only if the method
     * <ul>
     * <li>is not null,</li>
     * <li>is public,</li>
     * <li>is static,</li>
     * <li>has no parameters, and</li>
     * <li>returns an {@link Object}{@code [][]}.</li>
     * </ul>
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param dataProviderMethod the method to check
     * @return true if the method is a valid data provider, false otherwise
     */
    boolean isValidDataProviderMethod(FrameworkMethod dataProviderMethod) {
        // @formatter:off
        return dataProviderMethod != null
                && Modifier.isPublic(dataProviderMethod.getMethod().getModifiers())
                && Modifier.isStatic(dataProviderMethod.getMethod().getModifiers())
                && dataProviderMethod.getMethod().getParameterTypes().length == 0
                && dataProviderMethod.getMethod().getReturnType().equals(Object[][].class);
        // @formatter:on
    }

    /**
     * Creates a list of test methods out of an existing test method and its data provider method.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param testMethod the original test method
     * @param dataProviderMethod the data provider method that gives the parameters
     * @return a list of methods, each method bound to a parameter combination returned by the data provider
     */
    List<FrameworkMethod> explodeTestMethod(FrameworkMethod testMethod, FrameworkMethod dataProviderMethod) {
        int idx = 0;
        List<FrameworkMethod> result = new ArrayList<FrameworkMethod>();

        Object[][] dataProviderMethodResult;
        try {
            dataProviderMethodResult = (Object[][]) dataProviderMethod.invokeExplosively(null);
        } catch (Throwable t) {
            throw new Error(String.format("Exception while exploding test method using data provider '%s': %s",
                    dataProviderMethod.getName(), t.getMessage()), t);
        }
        if (dataProviderMethodResult == null) {
            throw new Error(String.format("Data provider method '%s' must not return 'null'.",
                    dataProviderMethod.getName()));
        }
        if (dataProviderMethodResult.length == 0) {
            throw new Error(String.format("Data provider '%s' must not return an empty object array.",
                    dataProviderMethod.getName()));
        }

        for (Object[] parameters : dataProviderMethodResult) {
            result.add(new DataProviderFrameworkMethod(testMethod.getMethod(), idx++, parameters));
        }
        return result;
    }

    /**
     * Returns a {@link TestClass} object wrapping the class to be executed. This method is required for testing because
     * {@link #getTestClass()} is final and therefore cannot be stubbed :(
     */
    TestClass getTestClassInt() {
        return getTestClass();
    }
}
