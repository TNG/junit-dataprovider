package com.tngtech.test.junit.dataprovider.custom.converter;

import org.junit.platform.commons.support.ReflectionSupport;

import com.tngtech.junit.dataprovider.DataProviderInvocationContextProvider;
import com.tngtech.junit.dataprovider.DisplayNameContext;
import com.tngtech.junit.dataprovider.convert.ConverterContext;

class CustomConverterDataProviderExtension extends DataProviderInvocationContextProvider<CustomConverterDataProvider> {

    CustomConverterDataProviderExtension() {
        super(CustomConverterDataProvider.class);
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

    @Override
    protected DisplayNameContext getDisplayNameContext(CustomConverterDataProvider annotation) {
        return new DisplayNameContext(annotation.format(), getDefaultPlaceholders());
    }
}
