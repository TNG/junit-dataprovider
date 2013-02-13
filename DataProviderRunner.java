package com.tngtech.java.junit.dataprovider;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * A custom runner for JUnit that allows the usage of TestNG-like data providers. Data providers are public,
 * static methods that return an <code>Object[][]</code>, see {@link DataProvider}.<br/>
 * You test method must be annotated with {@link UseDataProvider} instead of {@link Test}.
 */
public class DataProviderRunner extends BlockJUnit4ClassRunner {

    /**
     * A special framework method that allows the usage of parameters for the test method.
     */
    protected static class DataProviderFrameworkMethod extends FrameworkMethod {

        /** parameters to invoke the test method */
        protected final Object[] parameters;

        /** flag if the last parameter is the expected value and should not be included in the name */
        protected boolean hasExpectedParameter;

        public DataProviderFrameworkMethod(Method method, Object[] parameters, boolean hasExpectedParameter) {
            super(method);
            this.parameters = Arrays.copyOf(parameters, parameters.length);
            this.hasExpectedParameter = hasExpectedParameter;
        }

        @Override
        public String getName() {
            // don't print last value, it is the expected one
            return super.getName()
                    + ": "
                    + formatParameters(Arrays.copyOf(parameters, hasExpectedParameter ? parameters.length - 1
                            : parameters.length));
        }

        @Override
        public Object invokeExplosively(Object target, Object... params) throws Throwable {
            return super.invokeExplosively(target, parameters);
        }

        @Override
        public boolean equals(Object other) {
            if (!DataProviderFrameworkMethod.class.isInstance(other) || !super.equals(other)) {
                return false;
            }

            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(parameters, ((DataProviderFrameworkMethod) other).parameters);
            return equalsBuilder.isEquals();
        }

        @Override
        public int hashCode() {
            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
            hashCodeBuilder.appendSuper(super.hashCode());
            hashCodeBuilder.append(parameters);
            return hashCodeBuilder.hashCode();
        }

        /**
         * Returns a string representation of the given parameters. The parameters are converted to string by
         * the following rules:
         * <ul>
         * <li>null -&gt; &lt;null&gt;</li>
         * <li>empty string -&gt; &lt;empty string&gt;</li>
         * <li>other -&gt; Object.toString</li>
         * </ul>
         * 
         * @param parameters
         *            the parameters are converted to a comma-separated string
         * @return a string representation of the given parameters
         */
        protected String formatParameters(Object[] parameters) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i] != null) {
                    if (parameters[i] instanceof String && parameters[i].toString().isEmpty()) {
                        stringBuilder.append("<empty string>");
                    } else {
                        stringBuilder.append(parameters[i]);
                    }
                } else {
                    stringBuilder.append("<null>");
                }
                if (i < parameters.length - 1) {
                    stringBuilder.append(", ");
                }
            }

            return stringBuilder.toString();
        }
    }

    /**
     * Creates a DataProviderRunner to run {@code clazz}
     * 
     * @param clazz
     *            the test class to run
     * @throws InitializationError
     *             if the test class is malformed.
     */
    public DataProviderRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> testMethods = super.computeTestMethods();

        for (FrameworkMethod testMethod : new ArrayList<FrameworkMethod>(testMethods)) {
            if (!(testMethod instanceof DataProviderFrameworkMethod)) {
                FrameworkMethod dataProviderMethod = getDataProviderMethod(testMethod);
                if (isValidDataProviderMethod(dataProviderMethod)) {
                    List<FrameworkMethod> explodedTestMethods = explodeTestMethods(testMethod,
                            dataProviderMethod);

                    // remove first, otherwise duplicates are inserted
                    testMethods.removeAll(explodedTestMethods);
                    testMethods.addAll(explodedTestMethods);
                }
            }
        }

        return testMethods;
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        super.collectInitializationErrors(errors);
        validateDataProvider(errors);
    }

    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        for (FrameworkMethod method : computeTestMethods()) {
            if (method.getAnnotation(UseDataProvider.class) == null) {
                method.validatePublicVoidNoArg(false, errors);
            }
        }
    }

    /**
     * Validates test methods and their data providers. This method cannot use the result of
     * {@link DataProviderRunner#computeTestMethods()} because the method ignores invalid test methods and
     * data providers silently (except if a data provider method cannot be called). However, The common errors
     * are not raised as {@link RuntimeException} to go the JUnit way of detecting errors. This implies that
     * we have to browse the whole class for test methods and data providers again ;-(.
     * 
     * @param errors
     *            errors are added to this list
     */
    protected void validateDataProvider(List<Throwable> errors) {
        for (FrameworkMethod testMethod : getTestClass().getAnnotatedMethods(UseDataProvider.class)) {
            UseDataProvider testWithDataProvider = testMethod.getAnnotation(UseDataProvider.class);
            String dataProviderName = testWithDataProvider.value();
            FrameworkMethod dataProviderMethod = getDataProviderMethod(testMethod);
            if (dataProviderMethod == null) {
                errors.add(new Error("No such data provider: " + dataProviderName));
            } else if (!isValidDataProviderMethod(dataProviderMethod)) {
                errors.add(new Error(
                        "The data provider method '"
                                + dataProviderName
                                + "' is not valid. "
                                + "A valid method is public, static, receives no parameters and returns the the Object[][]!"));
            }
        }
    }

    /**
     * Creates a list of test methods out of an existing test method and its data provider method.
     * 
     * @param testMethod
     *            the original test method
     * @param dataProviderMethod
     *            the data provider method that gives the parameters
     * @return a list of methods, each method bound to a parameter combination returned by the data provider
     */
    protected List<FrameworkMethod> explodeTestMethods(FrameworkMethod testMethod,
            FrameworkMethod dataProviderMethod) {
        List<FrameworkMethod> testMethods = new ArrayList<FrameworkMethod>();

        try {
            Object[][] parameterList = (Object[][]) dataProviderMethod.invokeExplosively(null,
                    new Object[] {});
            for (Object[] parameters : parameterList) {
                DataProviderFrameworkMethod dataProviderFrameworkMethod = new DataProviderFrameworkMethod(
                        testMethod.getMethod(), parameters, dataProviderMethod.getAnnotation(
                                DataProvider.class).expectedParameter());

                testMethods.add(dataProviderFrameworkMethod);
            }
        } catch (Throwable exception) {
            throw new RuntimeException(exception);
        }

        return testMethods;
    }

    /**
     * Checks if a data provider with the given name exists.
     * 
     * @param dataProviderName
     *            name of the data provider
     * @return true if the provider exists, false otherwise.
     */
    protected boolean existsDataProvider(String dataProviderName) {
        return getDataProviderMethod(dataProviderName) != null;
    }

    /**
     * Returns the data provider method with the given name. Returns null if no such provider exists.
     * 
     * @param dataProviderName
     *            name of the data provider
     * @return the data provider or null if no such data provider exists
     */
    protected FrameworkMethod getDataProviderMethod(String dataProviderName) {
        for (FrameworkMethod method : getTestClass().getAnnotatedMethods(DataProvider.class)) {
            if (method.getName().equals(dataProviderName)) {
                return method;
            }
        }

        return null;
    }

    /**
     * Returns the data provider method that belongs to the given test method. Returns null if no such
     * provider exists or the test method is not marked for usage of a data provider
     * 
     * @param testMethod
     *            test method that uses a data provider
     * @return the data provider or null if no such data provider exists
     */
    protected FrameworkMethod getDataProviderMethod(FrameworkMethod testMethod) {
        UseDataProvider testUsingDataProvider = testMethod.getAnnotation(UseDataProvider.class);
        if (testUsingDataProvider == null) {
            return null;
        }

        String dataProviderName = testUsingDataProvider.value();
        return getDataProviderMethod(dataProviderName);
    }

    /**
     * Checks if the given method is a valid data provider. A method is a valid data provider if and only if
     * the method
     * <ul>
     * <li>is not null</li>
     * <li>is public</li>
     * <li>is static</li>
     * <li>has no parameters</li>
     * <li>returns an Object[][]</li>
     * </ul>
     * 
     * @param dataProviderMethod
     *            the method to check
     * @return true if the method is a valid data provider, false otherwise
     */
    protected boolean isValidDataProviderMethod(FrameworkMethod dataProviderMethod) {
        return dataProviderMethod != null && Modifier.isPublic(dataProviderMethod.getMethod().getModifiers())
                && Modifier.isStatic(dataProviderMethod.getMethod().getModifiers())
                && dataProviderMethod.getMethod().getParameterTypes().length == 0
                && dataProviderMethod.getMethod().getReturnType().equals((new Object[][] { {} }).getClass());
    }
}

