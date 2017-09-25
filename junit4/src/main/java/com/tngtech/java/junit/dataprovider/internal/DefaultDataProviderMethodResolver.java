package com.tngtech.java.junit.dataprovider.internal;

import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkNotNull;
import static java.lang.Character.toUpperCase;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderMethodResolver;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

/**
 * Default implementation to resolve the dataprovider method for a test method using {@link UseDataProvider} annotation.
 * <p>
 * It is being tried to resolve the dataprovider method using various strategies. The <b>location</b>s of the dataprovider can optionally
 * specified using {@link UseDataProvider#location()}. If no specific location is specified, test class itself is used (= where
 * {@code @}{@link UseDataProvider} annotation is used). If multiple locations are specified, each is searched for an appropriate
 * dataprovider.
 * <p>
 * The <b>name</b> of the dataprovider method can be explicitly set via {@link UseDataProvider#value()} or derived by convention. The first
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
     * @return the resolved dataprovider method or an empty {@link List} if dataprovider could not be found (never null)
     * @see DefaultDataProviderMethodResolver
     */
    @Override
    public List<FrameworkMethod> resolve(FrameworkMethod testMethod, UseDataProvider useDataProvider) {
        checkNotNull(testMethod, "testMethod must not be null");
        checkNotNull(useDataProvider, "useDataProvider must not be null");

        List<TestClass> dataProviderLocations = findDataProviderLocations(testMethod, useDataProvider.location());
        return findDataProviderMethods(dataProviderLocations, testMethod.getName(), useDataProvider.value());
    }

    protected List<TestClass> findDataProviderLocations(FrameworkMethod testMethod, Class<?>[] useDataProviderLocation) {
        if (useDataProviderLocation.length == 0) {
            return singletonList(new TestClass(testMethod.getMethod().getDeclaringClass()));
        }

        List<TestClass> result = new ArrayList<TestClass>();
        for (Class<?> location : useDataProviderLocation) {
            result.add(new TestClass(location));
        }
        return result;
    }

    protected List<FrameworkMethod> findDataProviderMethods(List<TestClass> locations, String testMethodName, String useDataProviderValue) {
        List<FrameworkMethod> result = new ArrayList<FrameworkMethod>();
        for (TestClass location : locations) {
            FrameworkMethod method = findDataProviderMethod(location, testMethodName, useDataProviderValue);
            if (method != null) {
                result.add(method);
            }
        }
        return result;
    }

    protected FrameworkMethod findDataProviderMethod(TestClass location, String testMethodName, String useDataProviderValue) {
        List<FrameworkMethod> dataProviderMethods = location.getAnnotatedMethods(DataProvider.class);

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
