package com.tngtech.junit.dataprovider;

import org.junit.platform.commons.support.ReflectionSupport;

import com.tngtech.junit.dataprovider.convert.ConverterContext;

/**
 * Default implementation for using a dataprovider that provides the data directly within itself.
 */
public class DataProviderExtension extends DataProviderInvocationContextProvider<DataProvider> {

    DataProviderExtension() {
        super(DataProvider.class);
    }

    @Override
    protected Object getData(DataProvider dataProvider) {
        return dataProvider.value();
    }

    @Override
    protected ConverterContext getConverterContext(DataProvider dataProvider) {
        return new ConverterContext(ReflectionSupport.newInstance(dataProvider.objectArrayConverter()),
                ReflectionSupport.newInstance(dataProvider.singleArgConverter()),
                ReflectionSupport.newInstance(dataProvider.stringConverter()), dataProvider.splitBy(),
                dataProvider.convertNulls(), dataProvider.trimValues(), dataProvider.ignoreEnumCase());
    }

    @Override
    protected DisplayNameContext getDisplayNameContext(DataProvider dataProvider) {
        return new DisplayNameContext(dataProvider.formatter(), dataProvider.format(), getDefaultPlaceholders());
    }
}
