package com.tngtech.test.junit.dataprovider.custom.resolver;

import com.tngtech.junit.dataprovider.resolver.DataProviderMethodResolver;
import com.tngtech.junit.dataprovider.resolver.DefaultDataProviderMethodResolver;

/**
 * {@link DataProviderMethodResolver} which uses all dataproviders prefixed with the name of the {@code testMethod}.
 */
class DataProviderStartWithTestMethodNameResolver extends DefaultDataProviderMethodResolver {
    @Override
    protected boolean isMatchingNameConvention(String testMethodName, String dataProviderMethodName) {
        return dataProviderMethodName.startsWith(testMethodName);
    }
}