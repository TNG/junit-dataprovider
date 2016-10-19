package com.tngtech.java.junit.dataprovider.internal;

import static java.lang.Character.toUpperCase;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderMethodResolver;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

/**
 * Default implementation to resolve the dataprovider method for a test method using {@link UseDataProvider} annotation.
 * <p>
 * It is being tried to resolve the dataprovider method using various strategies. The <b>location</b> of the dataprovider can optionally
 * specified using {@link UseDataProvider#location()}. If no specific location is specified, test class itself is used (= where
 * {@code @}{@link UseDataProvider} annotation is used). If multiple locations are specified, only the first class will be considered. The
 * <b>name</b> of the dataprovider method can be explicitly set via {@link UseDataProvider#value()} or derived by convention. The first
 * found dataprovider method will be used. Here are the applied strategies:
 * <ul>
 * <li>Explicitly configured name using {@link UseDataProvider#value()}. (no fallback if dataprovider could not be found)</li>
 * <li>@{@link DataProvider} annotated method which name equals the test method name</li>
 * <li>@{@link DataProvider} annotated method whereby prefix is replaced by one out of the following:
 * <table border="1" summary="Prefix replacement overview.">
 * <tr>
 * <th>prefix</th>
 * <th>replacement</th>
 * </tr>
 * <tr>
 * <td>test</td>
 * <td>dataProvider</td>
 * </tr>
 * <tr>
 * <td>test</td>
 * <td>data</td>
 * </tr>
 * </table>
 * </li>
 * <li>@{@link DataProvider} annotated method whereby additional prefix "dataProvider" or "data" is given. Also the first letter of the
 * original test method name is uppercased, e.g. {@code shouldReturnTwoForOnePlusOne} corresponds to
 * {@code dataProviderShouldReturnTwoForOnePlusOne}.</li>
 * </ul>
 */
public class DefaultDataProviderMethodResolver implements DataProviderMethodResolver {

    /**
     * {@inheritDoc}
     * <p>
     * Look at the classes java doc for detailed description of the applied strategies.
     *
     * @see DefaultDataProviderMethodResolver
     */
    @Override
    public FrameworkMethod resolve(FrameworkMethod testMethod, UseDataProvider useDataProvider) {
        if (testMethod == null) {
            throw new IllegalArgumentException("testMethod must not be null");
        }
        if (useDataProvider == null) {
            throw new IllegalArgumentException("useDataProvider must not be null");
        }

        TestClass dataProviderClass = findDataProviderLocation(testMethod, useDataProvider);
        List<FrameworkMethod> dataProviderMethods = dataProviderClass.getAnnotatedMethods(DataProvider.class);
        return findDataProviderMethod(dataProviderMethods, useDataProvider.value(), testMethod.getName());
    }

    protected TestClass findDataProviderLocation(FrameworkMethod testMethod, UseDataProvider useDataProvider) {
        if (useDataProvider.location().length == 0) {
            return new TestClass(testMethod.getMethod().getDeclaringClass());
        }
        return new TestClass(useDataProvider.location()[0]);
    }

    private FrameworkMethod findDataProviderMethod(List<FrameworkMethod> dataProviderMethods, String useDataProviderValue,
            String testMethodName) {
        if (!UseDataProvider.DEFAULT_VALUE.equals(useDataProviderValue)) {
            return findMethod(dataProviderMethods, useDataProviderValue);
        }

        FrameworkMethod result = findMethod(dataProviderMethods, testMethodName);
        if (result == null) {
            String dataProviderMethodName = testMethodName.replaceAll("^test", "dataProvider");
            result = findMethod(dataProviderMethods, dataProviderMethodName);
        }
        if (result == null) {
            String dataProviderMethodName = testMethodName.replaceAll("^test", "data");
            result = findMethod(dataProviderMethods, dataProviderMethodName);
        }
        if (result == null) {
            String dataProviderMethodName = "dataProvider" + toUpperCase(testMethodName.charAt(0)) + testMethodName.substring(1);
            result = findMethod(dataProviderMethods, dataProviderMethodName);
        }
        if (result == null) {
            String dataProviderMethodName = "data" + toUpperCase(testMethodName.charAt(0)) + testMethodName.substring(1);
            result = findMethod(dataProviderMethods, dataProviderMethodName);
        }
        return result;
    }

    private FrameworkMethod findMethod(List<FrameworkMethod> methods, String methodName) {
        for (FrameworkMethod method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}
