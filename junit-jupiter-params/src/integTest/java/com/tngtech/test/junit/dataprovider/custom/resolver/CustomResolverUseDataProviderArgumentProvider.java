package com.tngtech.test.junit.dataprovider.custom.resolver;

import static java.util.Arrays.asList;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.ReflectionSupport;

import com.tngtech.junit.dataprovider.AbstractUseDataProviderArgumentProvider;
import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext;

class CustomResolverUseDataProviderArgumentProvider
        extends AbstractUseDataProviderArgumentProvider<CustomResolverUseDataProvider, DataProvider> {

    CustomResolverUseDataProviderArgumentProvider() {
        super(DataProvider.class);
    }

    @Override
    public void accept(CustomResolverUseDataProvider sourceAnnotation) {
        this.sourceAnnotation = sourceAnnotation;
    }

    @Override
    protected DataProviderResolverContext getDataProviderResolverContext(ExtensionContext extensionContext,
            CustomResolverUseDataProvider annotation) {
        return new DataProviderResolverContext(extensionContext.getRequiredTestMethod(), asList(annotation.resolver()),
                annotation.resolveStrategy(), asList(annotation.location()), DataProvider.class, annotation.value());
    }

    @Override
    protected ConverterContext getConverterContext(DataProvider dataProvider) {
        return new ConverterContext(ReflectionSupport.newInstance(dataProvider.objectArrayConverter()),
                ReflectionSupport.newInstance(dataProvider.singleArgConverter()),
                ReflectionSupport.newInstance(dataProvider.stringConverter()), dataProvider.splitBy(),
                dataProvider.convertNulls(), dataProvider.trimValues(), dataProvider.ignoreEnumCase());
    }
}
