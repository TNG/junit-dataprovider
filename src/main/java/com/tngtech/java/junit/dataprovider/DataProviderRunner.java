package com.tngtech.java.junit.dataprovider;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import com.tngtech.java.junit.dataprovider.internal.FrameworkMethodGenerator;

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
     * The {@link FrameworkMethodGenerator} to be used to generate all framework methods to be executed as test
     * (enhanced by data providers data if desired).
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    FrameworkMethodGenerator frameworkMethodGenerator;

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
    protected void collectInitializationErrors(List<Throwable> errors) {
        // initialize frameworkMethodGenerator and dataConverter here because "super" in constructor already calls this,
        // i.e. fields are not initialized yet but required in super.collectInitializationErrors(errors) ...
        dataConverter = new DataConverter();
        frameworkMethodGenerator = new FrameworkMethodGenerator(dataConverter);

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
        for (FrameworkMethod testMethod : getTestClassInt().getAnnotatedMethods(Test.class)) {
            UseDataProvider useDataProvider = testMethod.getAnnotation(UseDataProvider.class);
            DataProvider dataProvider = testMethod.getAnnotation(DataProvider.class);

            if (useDataProvider != null && dataProvider != null) {
                errors.add(new Exception(String.format("Method %s() should either have @%s or @%s annotation",
                        testMethod.getName(), useDataProvider.getClass().getSimpleName(), dataProvider.getClass()
                                .getSimpleName())));

            } else if (useDataProvider == null && dataProvider == null) {
                testMethod.validatePublicVoidNoArg(false, errors);

            } else {
                testMethod.validatePublicVoid(false, errors);
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
            FrameworkMethod dataProviderMethod = getDataProviderMethod(testMethod);
            if (dataProviderMethod == null) {
                errors.add(new Exception("No such data provider: "
                        + testMethod.getAnnotation(UseDataProvider.class).value()));

            } else {
                validateDataProviderMethod(dataProviderMethod, errors);
            }
        }
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
            result.addAll(frameworkMethodGenerator.generateExplodedTestMethodsFor(testMethod, dataProviderMethod));
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
     * Checks if the given {@code dataProviderMethod} is a valid data provider and adds a {@link Throwable} to
     * {@code errors} if it
     * <ul>
     * <li>is not public,</li>
     * <li>is not static,</li>
     * <li>takes parameters, or</li>
     * <li>does return a convertable type, see {@link DataConverter#canConvert(Type)}
     * </ul>
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     * <ul>
     *
     * @param dataProviderMethod the method to check
     * @param errors to be "returned" and thrown as {@link InitializationError}
     */
    void validateDataProviderMethod(FrameworkMethod dataProviderMethod, List<Throwable> errors) {
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
