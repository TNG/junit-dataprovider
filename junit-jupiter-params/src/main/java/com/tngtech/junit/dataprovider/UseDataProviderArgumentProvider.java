package com.tngtech.junit.dataprovider;

import static java.util.Arrays.asList;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.ReflectionSupport;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext;

/**
 * Default implementation for using a dataprovider with an "external" dataprovider providing the test data.
 */
class UseDataProviderArgumentProvider
        extends AbstractUseDataProviderArgumentProvider<UseDataProvider, DataProvider> {

    UseDataProviderArgumentProvider() {
        super(DataProvider.class);
    }

    @Override
    public void accept(UseDataProvider sourceAnnotation) {
        this.sourceAnnotation = sourceAnnotation;
    }

    @Override
    protected DataProviderResolverContext getDataProviderResolverContext(ExtensionContext extensionContext,
            UseDataProvider testAnnotation) {
        return new DataProviderResolverContext(extensionContext.getRequiredTestMethod(),
                asList(sourceAnnotation.resolver()), sourceAnnotation.resolveStrategy(),
                asList(sourceAnnotation.location()), DataProvider.class, sourceAnnotation.value());
    }

    @Override
    protected ConverterContext getConverterContext(DataProvider dataProvider) {
        return new ConverterContext(ReflectionSupport.newInstance(dataProvider.objectArrayConverter()),
                ReflectionSupport.newInstance(dataProvider.singleArgConverter()),
                ReflectionSupport.newInstance(dataProvider.stringConverter()), dataProvider.splitBy(),
                dataProvider.convertNulls(), dataProvider.trimValues(), dataProvider.ignoreEnumCase());
    }
}
