package com.tngtech.junit.dataprovider.resolver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Interface to be used to implement a dataprovider method resolver based on the given {@link DataProviderResolverContext}.
 */
public interface DataProviderMethodResolver {

    /**
     * Returns the dataprovider methods that belongs to the given test method using the given resolver context or an
     * empty {@link List} if no dataprovider method could be found.
     *
     * @param context for resolving of dataprovider methods
     * @return the resolved dataprovider methods or an empty {@link List} if no dataprovider methods could be found
     * @throws NullPointerException if given {@code context} is {@code null}
     */
    List<Method> resolve(DataProviderResolverContext context);
}
