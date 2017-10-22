package com.tngtech.test.junit.dataprovider.custom.meta;

import com.tngtech.junit.dataprovider.AbstractStringDataProviderArgumentProvider;
import com.tngtech.junit.dataprovider.convert.ConverterContext;

class StringDataProviderProvider extends AbstractStringDataProviderArgumentProvider<StringDataProvider> {

    @Override
    public void accept(StringDataProvider sourceAnnotation) {
        this.sourceAnnotation = sourceAnnotation;
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
}
