package com.tngtech.test.junit.dataprovider.custom.meta;

import com.tngtech.junit.dataprovider.DataProviderInvocationContextProvider;
import com.tngtech.junit.dataprovider.DisplayNameContext;
import com.tngtech.junit.dataprovider.convert.ConverterContext;

class StringDataProviderExtension extends DataProviderInvocationContextProvider<StringDataProvider> {

    StringDataProviderExtension() {
        super(StringDataProvider.class);
    }

    @Override
    protected Object getData(StringDataProvider annotation) {
        return annotation.value();
    }

    @Override
    protected ConverterContext getConverterContext(StringDataProvider annotation) {
        return new ConverterContext(annotation.splitBy(), annotation.convertNulls(), annotation.trimValues(),
                annotation.ignoreEnumCase());
    }

    @Override
    protected DisplayNameContext getDisplayNameContext(StringDataProvider annotation) {
        return new DisplayNameContext(annotation.formatter(), annotation.format(), getDefaultPlaceholders());
    }
}
