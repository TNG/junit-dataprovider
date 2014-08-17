package com.tngtech.java.junit.dataprovider;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import com.tngtech.java.junit.dataprovider.internal.DataConverter;
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

    /** Empty {@link Statement} which does nothing at {@link Statement#evaluate()}. */
    private static final Statement STATEMENT_EMPTY = new Statement() {
        @Override
        public void evaluate() {
            // do nothing
        }
    };

    /**
     * The {@link DataConverter} to be used to convert from supported return types of any dataprovider to {@link List}
     * {@code <}{@link Object}{@code []>} such that data can be further handled.
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    DataConverter dataConverter;

    /**
     * The {@link TestValidator} to be used to validate all test methods to be executed as test and all dataprovider to
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
     * Stored failure within processing of {@code @}{@link BeforeClass} methods in {@link #invokeBeforeClass()} for
     * later processing in {@link #run(RunNotifier)}.
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    Throwable failure;

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
     * Additionally validates dataproviders.
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
        // and dataproviders silently (except if a dataprovider method cannot be called). However, the common errors
        // are not raised as {@link RuntimeException} to go the JUnit way of detecting errors. This implies that we have
        // to browse the whole class for test methods and dataproviders again :-(.

        for (FrameworkMethod testMethod : getTestClassInt().getAnnotatedMethods(Test.class)) {
            testValidator.validateTestMethod(testMethod, errors);
        }
        for (FrameworkMethod testMethod : getTestClassInt().getAnnotatedMethods(UseDataProvider.class)) {
            FrameworkMethod dataProviderMethod = getDataProviderMethod(testMethod);
            if (dataProviderMethod == null) {
                errors.add(new Exception("No such dataprovider: "
                        + testMethod.getAnnotation(UseDataProvider.class).value()));
            } else {
                DataProvider dataProvider = dataProviderMethod.getAnnotation(DataProvider.class);
                if (dataProvider == null) {
                    throw new IllegalStateException(String.format("@%s annotaion not found on dataprovider method %s",
                            DataProvider.class.getSimpleName(), dataProviderMethod.getName()));
                }
                testValidator.validateDataProviderMethod(dataProviderMethod, dataProvider, errors);
            }
        }
    }

    @Override
    public Statement classBlock(final RunNotifier notifier) {
        return super.classBlock(notifier);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden due to {@code @}{@link BeforeClass} methods are already processed, see {@link #computeTestMethods()}
     * and {@link #invokeBeforeClass()}. Just add a {@link Statement} which is processing potential caught
     * {@link Throwable} while {@code @BeforeClass} methods have been executed before.
     *
     * @return {@code Statement} to be evaluated
     */
    @Override
    protected Statement withBeforeClasses(final Statement statement) {
        // Instead of calling withBeforeClasses(statement) just re-throw failure while it was processed before
        Statement newStatement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (failure != null) {
                    throw failure;
                }
                statement.evaluate();
            }
        };
        return newStatement;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Just overridden to make {@link #classBlock(RunNotifier)} testable. Just invokes {@code super}.
     *
     * @param notifier to be used while processing children
     * @return {@code Statement} to be evaluated to invoke children
     */
    @Override
    protected Statement childrenInvoker(RunNotifier notifier) {
        return super.childrenInvoker(notifier);
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
            invokeBeforeClass();
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
     * Runs {@code @}{@link BeforeClass} methods original implementation in {@link #withBeforeClasses(Statement)} and
     * {@link #run(RunNotifier)} would do it later. Stores possible {@link Exception}s in {@link #failure} that it can
     * be processed later in {@link #run(RunNotifier)}.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     */
    void invokeBeforeClass() {
        // run @BeforeClass methods before exploding test methods
        List<FrameworkMethod> befores = getTestClassInt().getAnnotatedMethods(BeforeClass.class);
        if (!befores.isEmpty()) {
            try {
                new RunBefores(STATEMENT_EMPTY, befores, null).evaluate();
            } catch (Throwable e) {
                failure = e;
            }
        }
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
     * Returns the dataprovider method that belongs to the given test method or {@code null} if no such dataprovider
     * exists or the test method is not marked for usage of a dataprovider
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param testMethod test method that uses a dataprovider
     * @return the dataprovider or {@code null} (if dataprovider does not exist or test method does not use any)
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
