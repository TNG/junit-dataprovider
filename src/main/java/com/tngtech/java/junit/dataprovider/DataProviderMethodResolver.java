package com.tngtech.java.junit.dataprovider;

import org.junit.runners.model.FrameworkMethod;

/**
 * Interface to be used to implement a dataprovider method resolver based on the test method. The resolver can be specified for test case
 * using {@link UseDataProvider#resolver()}. The provided resolvers are executed in order until the first resolver returns a proper
 * dataprovider method (= not {@code null}) or no more resolvers are available.
 *
 */
public interface DataProviderMethodResolver {

    /**
     * Returns the dataprovider method that belongs to the given test method or {@code null} if no such dataprovider method could be found.
     * If not found, the next provided resolver is executed if any.
     *
     * @param testMethod test method that uses a dataprovider
     * @param useDataProvider {@link UseDataProvider} annoation on the given test method
     * @return the resolved dataprovider method or {@code null} if dataprovider could not be found
     * @throws IllegalArgumentException if given {@code testMethod} is {@code null}
     */
    FrameworkMethod resolve(FrameworkMethod testMethod, UseDataProvider useDataProvider);
}
