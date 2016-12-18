package com.tngtech.java.junit.dataprovider.internal;

import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderFrameworkMethod;

public class TestGenerator {

    private final DataConverter dataConverter;

    public TestGenerator(DataConverter dataConverter) {
        this.dataConverter = checkNotNull(dataConverter, "dataConverter must not be null");
    }

    /**
     * Generates the exploded list of test methods for the given {@code testMethod}. The given {@link FrameworkMethod}
     * is checked if it uses the given dataprovider method, an {@code @}{@link DataProvider}, or nothing. If it uses any
     * dataprovider, for each line of the {@link DataProvider}s result a specific, parameterized test method will be
     * added. If not, the original test method is added. If the given test method is {@code null}, an empty list is
     * returned.
     *
     * @param testMethod the original test method
     * @param dataProviderMethod the corresponding dataprovider method or {@code null}
     * @return the exploded list of test methods or an empty list (never {@code null})
     * @throws Error if something went wrong while exploding test methods
     */
    public List<FrameworkMethod> generateExplodedTestMethodsFor(FrameworkMethod testMethod,
            FrameworkMethod dataProviderMethod) {

        if (testMethod == null) {
            return Collections.emptyList();
        }
        if (dataProviderMethod != null) {
            try {
                return explodeTestMethod(testMethod, dataProviderMethod);
            } catch (Exception e) {
                throw new Error(String.format("Cannot explode '%s.%s' using '%s' due to: %s", testMethod.getMethod()
                        .getDeclaringClass().getSimpleName(), testMethod.getName(), dataProviderMethod.getName(),
                        e.getMessage()), e);
            }
        }
        DataProvider dataProvider = testMethod.getAnnotation(DataProvider.class);
        if (dataProvider != null) {
            try {
                return explodeTestMethod(testMethod, dataProvider);
            } catch (Exception e) {
                throw new Error(String.format("Exception while exploding '%s.%s' using its '@DataProvider' due to: %s",
                        testMethod.getMethod().getDeclaringClass().getSimpleName(), testMethod.getName(),
                        e.getMessage()), e);
            }
        }
        return Arrays.asList(testMethod);
    }

    /**
     * Creates a list of test methods out of an existing test method and its dataprovider method.
     * <p>
     * This method is package private (= visible) for testing.
     * </p>
     *
     * @param testMethod the original test method
     * @param dataProviderMethod the dataprovider method that gives the parameters
     * @return a list of methods, each method bound to a parameter combination returned by the dataprovider
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
            throw new IllegalArgumentException(String.format("Exception while invoking dataprovider method '%s': %s",
                    dataProviderMethod.getName(), t.getMessage()), t);
        }

        return explodeTestMethod(testMethod, data, dataProviderMethod.getAnnotation(DataProvider.class));
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
        return explodeTestMethod(testMethod, dataProvider.value(), dataProvider);
    }

    private List<FrameworkMethod> explodeTestMethod(FrameworkMethod testMethod, Object data, DataProvider dataProvider) {
        Method method = testMethod.getMethod();
        List<Object[]> converted = dataConverter.convert(data, method.isVarArgs(), method.getParameterTypes(),
                dataProvider);
        if (converted.isEmpty()) {
            throw new IllegalArgumentException("Could not create test methods using probably 'null' or 'empty' dataprovider");
        }

        int idx = 0;
        List<FrameworkMethod> result = new ArrayList<FrameworkMethod>();
        for (Object[] parameters : converted) {
            result.add(new DataProviderFrameworkMethod(method, idx++, parameters, dataProvider.format()));
        }
        return result;
    }
}
