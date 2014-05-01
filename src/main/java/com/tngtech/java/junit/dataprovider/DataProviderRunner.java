package com.tngtech.java.junit.dataprovider;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
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

/**
 * A custom runner for JUnit that allows the usage of <a href="http://testng.org/">TestNG</a>-like data providers. Data
 * providers are public, static methods that return an {@link Object}{@code [][]} (see {@link DataProvider}).
 * <p>
 * Your test method must be annotated with {@code @}{@link UseDataProvider}, additionally.
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
    public void filter(Filter filter) throws NoTestsRemainException {
        Filter useFilter;
        if (!(filter instanceof CategoryFilter) && !isFilterBlackListed(filter)) {
            useFilter = new DataProviderFilter(filter);
        } else {
            useFilter = filter;
        }
        super.filter(useFilter);
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
            UseDataProvider useDataProviderAnnotation = method.getAnnotation(UseDataProvider.class);
            DataProvider dataProviderAnnotation = method.getAnnotation(DataProvider.class);

            if (useDataProviderAnnotation != null && dataProviderAnnotation != null) { // TODO Test
                errors.add(new Exception(String.format("Method %s() should either have %s or %s annotation",
                        method.getName(), UseDataProvider.class.getSimpleName(), DataProvider.class.getSimpleName())));
            } else if (useDataProviderAnnotation == null && dataProviderAnnotation == null) {
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
                DataProvider dataProvider = testMethod.getAnnotation(DataProvider.class);
                if (dataProvider == null) {
                    result.add(testMethod);

                } else {
                    result.addAll(explodeTestMethod(testMethod, dataProvider));
                }
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

    /**
     * Checks if the given method is a valid data provider. A method is a valid data provider if and only if the method
     * <ul>
     * <li>is not null,</li>
     * <li>is public,</li>
     * <li>is static,</li>
     * <li>has no parameters, and</li>
     * <li>returns
     * <ul>
     * <li>{@link Object}{@code [][]}, or</li>
     * <li>{@link List}{@code <}{@link List}{@code <}{@link Object}{@code >>}.</li>
     * </ul>
     * </ul>
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param dataProviderMethod the method to check
     * @return true if the method is a valid data provider, false otherwise
     */
    boolean isValidDataProviderMethod(FrameworkMethod dataProviderMethod) {
        if (dataProviderMethod == null) {
            return false;
        }

        Method method = dataProviderMethod.getMethod();

        // @formatter:off
        boolean result = Modifier.isPublic(method.getModifiers())
                && Modifier.isStatic(method.getModifiers())
                && method.getParameterTypes().length == 0;
        // @formatter:on

        if (result) {
            Class<?> returnClass = method.getReturnType();
            if (Object[][].class.equals(returnClass)) {
                return true;

            } else if (List.class.isAssignableFrom(returnClass)) {
                ParameterizedType returnType = (ParameterizedType) method.getGenericReturnType();
                if (returnType.getActualTypeArguments().length == 1
                        && returnType.getActualTypeArguments()[0] instanceof ParameterizedType) {
                    ParameterizedType type = (ParameterizedType) returnType.getActualTypeArguments()[0];
                    return List.class.isAssignableFrom((Class<?>) type.getRawType());
                }
            }
        }
        return false;
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

        try {
            Object data = dataProviderMethod.invokeExplosively(null);
            if (data instanceof Object[][]) {
                for (Object[] parameters : (Object[][]) data) {
                    result.add(new DataProviderFrameworkMethod(testMethod.getMethod(), idx++, parameters));
                }

            } else if (data instanceof List) {
                // must be List<List<Object>>, see #isValidDataProviderMethod
                @SuppressWarnings("unchecked")
                List<List<Object>> lists = (List<List<Object>>) data;
                for (List<Object> parameters : lists) {
                    result.add(new DataProviderFrameworkMethod(testMethod.getMethod(), idx++, parameters));
                }
            }
        } catch (Throwable t) {
            throw new Error(String.format("Exception while exploding test method using data provider '%s': %s",
                    dataProviderMethod.getName(), t.getMessage()), t);
        }

        if (result.isEmpty()) {
            throw new Error(String.format("Data provider '%s' must not be empty.", dataProviderMethod.getName()));
        }
        return result;
    }

    /**
     * TODO Creates a list of test methods out of an existing test method and its data provider method.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param testMethod the original test method
     * @param dataProviderMethod the data provider method that gives the parameters
     * @return a list of methods, each method bound to a parameter combination returned by the data provider
     */
    List<FrameworkMethod> explodeTestMethod(FrameworkMethod testMethod, DataProvider dataProvider) {
        List<FrameworkMethod> result = new ArrayList<FrameworkMethod>();

        try {
            String[] data = dataProvider.value();

            // TODO was ist mit typen der argumente?
            // TODO könnte man bei den validierungen schon überprüfen?
            int requiredArguments = testMethod.getMethod().getParameterTypes().length;
            int methodCount = data.length / requiredArguments;
            if (data.length % requiredArguments == 0) {
                for (int idx = 0; idx < methodCount; idx++) {
                    Object[] parameters = new Object[requiredArguments];
                    for (int jdx = 0; jdx < parameters.length; jdx++) {
                        parameters[jdx] = cast(data[idx * requiredArguments + jdx],
                                testMethod.getMethod().getParameterTypes()[jdx]);
                    }
                    result.add(new DataProviderFrameworkMethod(testMethod.getMethod(), idx++, parameters));
                }
            } else {
                // TODO exception
            }

        } catch (Throwable t) {
            throw new Error(String.format("Exception while exploding test method using %ss value attribute: %s",
                    dataProvider.getClass().getSimpleName(), t.getMessage()), t);
        }

        if (result.isEmpty()) {
            throw new Error(String.format("%s value must not be an empty array.", dataProvider.getClass()
                    .getSimpleName()));
        }
        return result;
    }

    /** TODO */
    private Object cast(String str, Class<?> toClass) {
        if (String.class.equals(toClass)) {
            return str;
        }

        if (boolean.class.equals(toClass)) {
            return Boolean.valueOf(str);
        }
        if (byte.class.equals(toClass)) {
            return Byte.valueOf(str);
        }
        if (char.class.equals(toClass)) {
            if (str.length() == 1) {
                return str.charAt(0);
            }
            throw new RuntimeException("2");
        }
        if (short.class.equals(toClass)) {
            return Short.valueOf(str);
        }
        if (int.class.equals(toClass)) {
            return Integer.valueOf(str);
        }
        if (long.class.equals(toClass)) {
            return Long.valueOf(str);
        }
        if (float.class.equals(toClass)) {
            return Float.valueOf(str);
        }
        if (double.class.equals(toClass)) {
            return Double.valueOf(str);
        }

        if (!toClass.isPrimitive()) {
            throw new RuntimeException("0"); // TODO
        }

        throw new RuntimeException("1"); // TODO
    }

    /**
     * Returns a {@link TestClass} object wrapping the class to be executed. This method is required for testing because
     * {@link #getTestClass()} is final and therefore cannot be stubbed :(
     */
    TestClass getTestClassInt() {
        return getTestClass();
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
}
