package com.tngtech.java.junit.dataprovider.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderFrameworkMethod;
import com.tngtech.java.junit.dataprovider.internal.DataConverter.Settings;

public class TestGenerator {

    private final DataConverter dataConverter;

    public TestGenerator(DataConverter dataConverter) {
        if (dataConverter == null) {
            throw new NullPointerException("dataConverter must not be null");
        }
        this.dataConverter = dataConverter;
    }

    /**
     * Generates the exploded list of test methods for the given {@code testMethod}. The given {@link FrameworkMethod}
     * is checked if it uses the given data provider method, an {@code @}{@link DataProvider}, or nothing. If it uses
     * any data provider, for each line of the {@link DataProvider}s result a specific, parameterized test method will
     * be added. If not, the original test method is added. If the given test method is {@code null}, an empty list is
     * returned.
     *
     * @param testMethod the original test method
     * @param dataProviderMethod the corresponding data provider method or {@code null}
     * @return the exploded list of test methods or an empty list (never {@code null})
     * @throws Error if something went wrong while exploding test methods
     */
    public List<FrameworkMethod> generateExplodedTestMethodsFor(FrameworkMethod testMethod,
            FrameworkMethod dataProviderMethod) {

        if (testMethod == null) {
            return Collections.emptyList();
        }
        if (dataProviderMethod != null) {
            return explodeTestMethod(testMethod, dataProviderMethod);
        }
        DataProvider dataProvider = testMethod.getAnnotation(DataProvider.class);
        if (dataProvider != null) {
            return explodeTestMethod(testMethod, dataProvider);
        }
        return Arrays.asList(testMethod);
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
        Object data;
        try {
            Class<?>[] parameterTypes = dataProviderMethod.getMethod().getParameterTypes();
            if (parameterTypes.length > 0) {
                data = dataProviderMethod.invokeExplosively(null, testMethod);
            } else {
                data = dataProviderMethod.invokeExplosively(null);
            }
        } catch (Throwable t) {
            throw new Error(String.format("Exception while invoking data provider method '%s': %s",
                    dataProviderMethod.getName(), t.getMessage()), t);
        }

        Settings settings = new Settings(dataProviderMethod.getAnnotation(DataProvider.class));

        List<Object[]> converted = dataConverter.convert(data, testMethod.getMethod().getParameterTypes(), settings);
        String emptyResultMessage = String.format("Data provider '%s' must neither be null nor empty but was: %s.",
                dataProviderMethod.getName(), data);
        return explodeTestMethod(testMethod, converted, emptyResultMessage);
    }

    /**
     * Creates a list of test methods out of an existing test method and its {@link DataProvider#value()} arguments.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param testMethod the original test method
     * @param dataProvider the {@link DataProvider} gives the parameters
     * @return a list of methods, each method bound to a parameter combination returned by the {@link DataProvider}
     */
    List<FrameworkMethod> explodeTestMethod(FrameworkMethod testMethod, DataProvider dataProvider) {
        String[] data = dataProvider.value();

        List<Object[]> converted = dataConverter.convert(data, testMethod.getMethod().getParameterTypes(),
                new Settings(dataProvider));
        String emptyResultMessage = String.format("%s.value() must be set but was: %s.", dataProvider.getClass()
                .getSimpleName(), Arrays.toString(data));
        return explodeTestMethod(testMethod, converted, emptyResultMessage);
    }

    private List<FrameworkMethod> explodeTestMethod(FrameworkMethod testMethod, List<Object[]> converted,
            String emptyResultMessage) {

        int idx = 0;
        List<FrameworkMethod> result = new ArrayList<FrameworkMethod>();
        for (Object[] parameters : converted) {
            result.add(new DataProviderFrameworkMethod(testMethod.getMethod(), idx++, parameters));
        }
        if (result.isEmpty()) {
            throw new Error(emptyResultMessage);
        }
        return result;
    }
}
