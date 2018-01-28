package com.tngtech.test.junit.dataprovider.custom.meta;

import static com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext.generateLocations;
import static java.util.Arrays.asList;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.ReflectionSupport;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.DisplayNameContext;
import com.tngtech.junit.dataprovider.UseDataProviderInvocationContextProvider;
import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext;

class DataProviderTestExtension extends UseDataProviderInvocationContextProvider<DataProviderTest, DataProvider> {

    DataProviderTestExtension() {
        super(DataProviderTest.class, DataProvider.class);
    }

    @Override
    protected DataProviderResolverContext getDataProviderResolverContext(ExtensionContext extensionContext,
            DataProviderTest annotation) {
        return new DataProviderResolverContext(extensionContext.getRequiredTestMethod(), asList(annotation.resolver()),
                annotation.resolveStrategy(),
                generateLocations(extensionContext.getRequiredTestClass(), annotation.location()), DataProvider.class,
                annotation.value());
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
        return new DisplayNameContext(dataProvider.format(), getDefaultPlaceholders());
    }
}
