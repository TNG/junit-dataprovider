package com.tngtech.java.junit.dataprovider;

import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import com.tngtech.java.junit.dataprovider.UseDataProvider.ResolveStrategy;
import com.tngtech.java.junit.dataprovider.internal.DataConverter;
import com.tngtech.java.junit.dataprovider.internal.DefaultDataProviderMethodResolver;
import com.tngtech.java.junit.dataprovider.internal.TestGenerator;
import com.tngtech.java.junit.dataprovider.internal.TestValidator;

/**
 * A custom runner for JUnit that allows the usage of <a href="http://testng.org/">TestNG</a>-like dataproviders. Data
 * providers are public, static methods that return an {@link Object}{@code [][]} (see {@link DataProvider}).
 * <p>
 * Your test method must be annotated with {@code @}{@link UseDataProvider} or {@code @}{@link DataProvider},
 * additionally.
 */
public class DataProviderRunner extends BlockJUnit4ClassRunner {

    /**
     * The {@link DataConverter} to be used to convert from supported return types of any dataprovider to {@link List}
     * {@code <}{@link Object}{@code []>} such that data can be further handled.
     */
    protected DataConverter dataConverter;

    /**
     * The {@link TestGenerator} to be used to generate all framework methods to be executed as test (enhanced by data
     * providers data if desired).
     */
    protected TestGenerator testGenerator;

    /**
     * The {@link TestValidator} to be used to validate all test methods to be executed as test and all dataprovider to
     * be used to explode tests.
     */
    protected TestValidator testValidator;

    /**
     * Cached result of {@link #computeTestMethods()}.
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    List<FrameworkMethod> computedTestMethods;

    /**
     * Cached result of {@link #getDataProviderMethods(FrameworkMethod)}.
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    Map<FrameworkMethod, List<FrameworkMethod>> dataProviderMethods;

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
        initializeHelpers();

        super.collectInitializationErrors(errors);
    }

    /**
     * Initialize and/or override {@link DataConverter}, {@link TestGenerator} and/or {@link TestValidator} helper classes.
     */
    protected void initializeHelpers() {
        dataConverter = new DataConverter();
        testGenerator = new TestGenerator(dataConverter);
        testValidator = new TestValidator(dataConverter);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Is copied from {@link BlockJUnit4ClassRunner#validateInstanceMethods} because {@link #computeTestMethods()} must
     * not be called if validation already found errors!
     */
    @Override
    @Deprecated
    protected void validateInstanceMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(After.class, false, errors);
        validatePublicVoidNoArgMethods(Before.class, false, errors);
        validateTestMethods(errors);

        if (errors.isEmpty() && computeTestMethods().size() == 0) {
            errors.add(new Exception("No runnable methods"));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Additionally validates dataproviders.
     *
     * @param errors that are added to this list
     * @throws NullPointerException if given {@code errors} is {@code null}
     */
    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        checkNotNull(errors, "errors must not be null");

        // This method cannot use the result of "computeTestMethods()" because the method ignores invalid test methods
        // and dataproviders silently (except if a dataprovider method cannot be called). However, the common errors
        // are not raised as {@link RuntimeException} to go the JUnit way of detecting errors. This implies that we have
        // to browse the whole class for test methods and dataproviders again :-(.

        for (FrameworkMethod testMethod : getTestClassInt().getAnnotatedMethods(Test.class)) {
            testValidator.validateTestMethod(testMethod, errors);
        }
        for (FrameworkMethod testMethod : getTestClassInt().getAnnotatedMethods(UseDataProvider.class)) {
            List<FrameworkMethod> dataProviderMethods = getDataProviderMethods(testMethod);
            if (dataProviderMethods.isEmpty()) {
                Class<? extends DataProviderMethodResolver>[] resolvers = testMethod.getAnnotation(UseDataProvider.class).resolver();

                String message = "No valid dataprovider found for test '" + testMethod.getName() + "' using ";
                if (resolvers.length == 1 && DefaultDataProviderMethodResolver.class.equals(resolvers[0])) {
                    message += "the default resolver. By convention the dataprovider method name must either be equal to the test methods name, have a certain replaced or additional prefix (see JavaDoc of "
                            + DefaultDataProviderMethodResolver.class + " or is explicitely set by @UseDataProvider#value()";
                } else {
                    message += "custom resolvers: " + Arrays.toString(resolvers)
                    + ". Please examine their javadoc and / or implementation.";
                }
                errors.add(new Exception(message));

            } else {
                for (FrameworkMethod dataProviderMethod : dataProviderMethods) {
                    DataProvider dataProvider = dataProviderMethod.getAnnotation(DataProvider.class);
                    if (dataProvider == null) {
                        throw new IllegalStateException(String.format("@%s annotation not found on dataprovider method %s",
                                DataProvider.class.getSimpleName(), dataProviderMethod.getName()));
                    }
                    testValidator.validateDataProviderMethod(dataProviderMethod, dataProvider, errors);
                }
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
     * a dataprovider.
     *
     * @param filter the {@link Filter} to be wrapped or apply, respectively
     */
    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        checkNotNull(filter, "filter must not be null");
        super.filter(new DataProviderFilter(filter));
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
            for (FrameworkMethod dataProviderMethod : getDataProviderMethods(testMethod)) {
                result.addAll(testGenerator.generateExplodedTestMethodsFor(testMethod, dataProviderMethod));
            }
        }
        return result;
    }

    /**
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     */
    List<FrameworkMethod> getDataProviderMethods(FrameworkMethod testMethod) {
        // initialize field here as this method is called via constructors super(...) => fields are not initialized yet
        if (dataProviderMethods == null) {
            dataProviderMethods = new HashMap<FrameworkMethod, List<FrameworkMethod>>();
        }
        if (dataProviderMethods.containsKey(testMethod)) {
            return dataProviderMethods.get(testMethod);
        }
        List<FrameworkMethod> result = new ArrayList<FrameworkMethod>();

        UseDataProvider useDataProvider = testMethod.getAnnotation(UseDataProvider.class);
        if (useDataProvider == null) {
            result.add(null);
        } else {
            for (Class<? extends DataProviderMethodResolver> resolverClass : useDataProvider.resolver()) {
                DataProviderMethodResolver resolver = getResolverInstanceInt(resolverClass);

                List<FrameworkMethod> dataProviderMethods = resolver.resolve(testMethod, useDataProvider);
                if (ResolveStrategy.UNTIL_FIRST_MATCH.equals(useDataProvider.resolveStrategy()) && !dataProviderMethods.isEmpty()) {
                    result.addAll(dataProviderMethods);
                    break;

                } else if (ResolveStrategy.AGGREGATE_ALL_MATCHES.equals(useDataProvider.resolveStrategy())) {
                    result.addAll(dataProviderMethods);
                }
            }
        }
        dataProviderMethods.put(testMethod, result);
        return result;
    }

    /**
     * Returns a new instance of {@link DataProviderMethodResolver}. This method is required for testing. It calls
     * {@link Class#newInstance()} which needs to be stubbed while testing.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     */
    DataProviderMethodResolver getResolverInstanceInt(Class<? extends DataProviderMethodResolver> resolverClass) {
        Constructor<? extends DataProviderMethodResolver> constructor;
        try {
            constructor = resolverClass.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Could not find default constructor to instantiate resolver " + resolverClass, e);
        } catch (SecurityException e) {
            throw new IllegalStateException(
                    "Security violation while trying to access default constructor to instantiate resolver " + resolverClass, e);
        }

        try {
            return constructor.newInstance();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not access default constructor to instantiate resolver " + resolverClass, e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Could not instantiate resolver " + resolverClass + " using default constructor", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The default constructor of " + resolverClass + " has thrown an exception", e);
        }
    }
}
