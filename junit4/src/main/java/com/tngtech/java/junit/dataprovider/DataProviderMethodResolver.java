package com.tngtech.java.junit.dataprovider;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;

/**
 * Interface to be used to implement a dataprovider method resolver based on the test method. The resolver can be specified for test case
 * using {@link UseDataProvider#resolver()}. The provided resolvers are executed according to the provide strategy in
 * {@link UseDataProvider#resolveStrategy()}.
 *
 */
public interface DataProviderMethodResolver {

    /**
     * Returns the dataprovider methods that belongs to the given test method or an empty {@link List} if no such dataprovider method could
     * be found.
     *
     * @param testMethod test method that uses a dataprovider
     * @param useDataProvider {@link UseDataProvider} annoation on the given test method
     * @return the resolved dataprovider methods or an empty {@link List} if dataprovider could not be found (never {@code null})
     * @throws IllegalArgumentException if given {@code testMethod} is {@code null}
     */
    List<FrameworkMethod> resolve(FrameworkMethod testMethod, UseDataProvider useDataProvider);
}
