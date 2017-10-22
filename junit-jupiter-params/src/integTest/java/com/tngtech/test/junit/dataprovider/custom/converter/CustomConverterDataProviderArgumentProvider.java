package com.tngtech.test.junit.dataprovider.custom.converter;

import org.junit.platform.commons.support.ReflectionSupport;

import com.tngtech.junit.dataprovider.AbstractStringDataProviderArgumentProvider;
import com.tngtech.junit.dataprovider.convert.ConverterContext;

class CustomConverterDataProviderArgumentProvider
        extends AbstractStringDataProviderArgumentProvider<CustomConverterDataProvider> {

    @Override
    public void accept(CustomConverterDataProvider sourceAnnotation) {
        this.sourceAnnotation = sourceAnnotation;
    }

    @Override
    protected Object getData(CustomConverterDataProvider annotation) {
        return annotation.value();
    }

    @Override
    protected ConverterContext getConverterContext(CustomConverterDataProvider annotation) {
        return new ConverterContext(ReflectionSupport.newInstance(annotation.objectArrayConverter()),
                ReflectionSupport.newInstance(annotation.singleArgConverter()),
                ReflectionSupport.newInstance(annotation.stringConverter()), annotation.splitBy(),
                annotation.convertNulls(), annotation.trimValues(), annotation.ignoreEnumCase());
    }
}
