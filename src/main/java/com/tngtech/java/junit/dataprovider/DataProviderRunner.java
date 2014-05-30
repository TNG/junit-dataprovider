package com.tngtech.java.junit.dataprovider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Categories.CategoryFilter;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import com.tngtech.java.junit.dataprovider.internal.DataConverter;
import com.tngtech.java.junit.dataprovider.internal.TestGenerator;
import com.tngtech.java.junit.dataprovider.internal.TestValidator;

/**
 * A custom runner for JUnit that allows the usage of <a href="http://testng.org/">TestNG</a>-like data providers. Data
 * providers are public, static methods that return an {@link Object}{@code [][]} (see {@link DataProvider}).
 * <p>
 * Your test method must be annotated with {@code @}{@link UseDataProvider} or {@code @}{@link DataProvider},
 * additionally.
 */
public class DataProviderRunner extends BlockJUnit4ClassRunner {

    /**
     * A list of filter packages which must not be wrapped by DataProviderRunner (this is a workaround for some plugins,
     * e.g. the maven-surefire-plugin).
     */
    // @formatter:off
    private static final List<String> BLACKLISTED_FILTER_PACKAGES = Arrays.asList(
            "org.apache.maven.surefire"
        );
    // @formatter:on

    /**
     * The {@link DataConverter} to be used to convert from supported return types of any data provider to {@link List}
     * {@code <}{@link Object}{@code []>} such that data can be further handled.
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    DataConverter dataConverter;

    /**
     * The {@link TestValidator} to be used to validate all test methods to be executed as test and all data provider to
     * be used to explode tests.
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    TestValidator testValidator;

    /**
     * The {@link TestGenerator} to be used to generate all framework methods to be executed as test (enhanced by data
     * providers data if desired).
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    TestGenerator testGenerator;


    /**
     * Cached result of {@link #computeTestMethods()}.
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
    protected void collectInitializationErrors(List<Throwable> errors) {
        // initialize testValidator, testGenerator and dataConverter here because "super" in constructor already calls
        // this, i.e. fields are not initialized yet but required in super.collectInitializationErrors(errors) ...
        dataConverter = new DataConverter();
        testValidator = new TestValidator(dataConverter);
        testGenerator = new TestGenerator(dataConverter);

        super.collectInitializationErrors(errors);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Additionally validates data providers.
     *
     * @param errors that are added to this list
     * @throws NullPointerException if given {@code errors} is {@code null}
     */
    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        if (errors == null) {
            throw new NullPointerException("errors must not be null");
        }

        // This method cannot use the result of "computeTestMethods()" because the method ignores invalid test methods
        // and data providers silently (except if a data provider method cannot be called). However, the common errors
        // are not raised as {@link RuntimeException} to go the JUnit way of detecting errors. This implies that we have
        // to browse the whole class for test methods and data providers again :-(.

        for (FrameworkMethod testMethod : getTestClassInt().getAnnotatedMethods(Test.class)) {
            testValidator.validateTestMethod(testMethod, errors);
        }
        for (FrameworkMethod testMethod : getTestClassInt().getAnnotatedMethods(UseDataProvider.class)) {
            FrameworkMethod dataProviderMethod = getDataProviderMethod(testMethod);
            if (dataProviderMethod == null) {
                errors.add(new Exception("No such data provider: "
                        + testMethod.getAnnotation(UseDataProvider.class).value()));
            } else {
                testValidator.validateDataProviderMethod(dataProviderMethod, errors);
            }
        }
    }

    /**
     * Generates the exploded list of methods that run tests. All methods annotated with {@code @Test} on this class and
     * super classes that are not overridden are checked if they use a {@code @}{@link DataProvider} or not. If yes, for
     * each row of the {@link DataProvider}s result a specific, parameterized test method will be added. If not, the
     * original test method is added.
     * <p>
     * Additionally, caches the result as {@link #computeTestMethods()} is call multiple times while test execution by
     * the JUnit framework (to validate, to filter, to execute, ...).
     *
     * @return the exploded list of test methods (never {@code null})
     */
    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        if (computedTestMethods == null) {
            // Further method for generation is required due to stubbing of "super.computeTestMethods()" is not possible
            computedTestMethods = generateExplodedTestMethodsFor(super.computeTestMethods());
        }
        return computedTestMethods;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If possible the given {@code filter} is wrapped by {@link DataProviderFilter} to enable filtering of tests using
     * a data provider.
     *
     * @param filter the {@link Filter} to wrap/apply
     */
    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        Filter useFilter;
        if (!(filter instanceof CategoryFilter) && !isFilterBlackListed(filter)) {
            useFilter = new DataProviderFilter(filter);
        } else {
            useFilter = filter;
        }
        super.filter(useFilter);
    }

    /**
     * Returns a {@link TestClass} object wrapping the class to be executed. This method is required for testing because
     * {@link #getTestClass()} is final and therefore cannot be stubbed :(
     */
    TestClass getTestClassInt() {
        return getTestClass();
    }

    /**
     * Generates the exploded list of test methods for the given {@code testMethods}. Each of the given
     * {@link FrameworkMethod}s is checked if it uses a {@code @}{@link DataProvider} or not. If yes, for each line of
     * the {@link DataProvider}s result a specific, parameterized test method will be added. If no, the original test
     * method is added.
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
            result.addAll(testGenerator.generateExplodedTestMethodsFor(testMethod, dataProviderMethod));
        }
        return result;
    }

    /**
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     */
    boolean isFilterBlackListed(Filter filter) {
        String className = filter.getClass().getName();
        for (String blacklistedPackage : BLACKLISTED_FILTER_PACKAGES) {
            if (className.startsWith(blacklistedPackage)) {
                return true;
            }
        }
        return false;
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
        UseDataProvider useDataProvider = testMethod.getAnnotation(UseDataProvider.class);
        if (useDataProvider == null) {
            return null;
        }

        TestClass dataProviderLocation = findDataProviderLocation(useDataProvider);
        for (FrameworkMethod method : dataProviderLocation.getAnnotatedMethods(DataProvider.class)) {
            if (method.getName().equals(useDataProvider.value())) {
                return method;
            }
        }
        return null;
    }

    /**
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     */
    TestClass findDataProviderLocation(UseDataProvider useDataProvider) {
        if (useDataProvider.location().length == 0) {
            return getTestClassInt();
        }
        return new TestClass(useDataProvider.location()[0]);
    }
}
