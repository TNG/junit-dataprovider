package com.tngtech.test.junit.dataprovider.custom.placeholder;

import java.util.List;

import org.junit.platform.commons.support.ReflectionSupport;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.DataProviderInvocationContextProvider;
import com.tngtech.junit.dataprovider.DisplayNameContext;
import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.placeholder.BasePlaceholder;

class CustomPlaceholderDataProviderExtension extends DataProviderInvocationContextProvider<DataProvider> {

    CustomPlaceholderDataProviderExtension() {
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
        @SuppressWarnings("unchecked")
        List<BasePlaceholder> defaultPlaceholders = (List<BasePlaceholder>) getDefaultPlaceholders();
        defaultPlaceholders.add(0, new StripArgumentLengthPlaceholder(10));
        return new DisplayNameContext(dataProvider.formatter(), dataProvider.format(), defaultPlaceholders);
    }
}
