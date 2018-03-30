package com.tngtech.junit.dataprovider;

import static com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext.generateLocations;
import static java.util.Arrays.asList;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.ReflectionSupport;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext;

/**
 * Default implementation for using a dataprovider with an "external" dataprovider providing the test data.
 */
public class UseDataProviderExtension extends UseDataProviderInvocationContextProvider<UseDataProvider, DataProvider> {

    UseDataProviderExtension() {
        super(UseDataProvider.class, DataProvider.class);
    }

    @Override
    protected DataProviderResolverContext getDataProviderResolverContext(ExtensionContext extensionContext,
            UseDataProvider testAnnotation) {
        return new DataProviderResolverContext(extensionContext.getRequiredTestMethod(),
                asList(testAnnotation.resolver()), testAnnotation.resolveStrategy(),
                generateLocations(extensionContext.getRequiredTestClass(), testAnnotation.location()),
                DataProvider.class, testAnnotation.value());
    }

    @Override
    protected ConverterContext getConverterContext(DataProvider dataProvider) {
        return new ConverterContext(ReflectionSupport.newInstance(dataProvider.objectArrayConverter()),
                ReflectionSupport.newInstance(dataProvider.singleArgConverter()),
                ReflectionSupport.newInstance(dataProvider.stringConverter()), dataProvider.splitBy(),
                dataProvider.convertNulls(), dataProvider.trimValues(), dataProvider.ignoreEnumCase());
    }

    @Override
    protected boolean cacheDataProviderResult(DataProvider dataProviderAnnotation) {
        return dataProviderAnnotation.cache();
    }

    @Override
    protected DisplayNameContext getDisplayNameContext(DataProvider dataProvider) {
        return new DisplayNameContext(dataProvider.formatter(), dataProvider.format(), getDefaultPlaceholders());
    }
}
