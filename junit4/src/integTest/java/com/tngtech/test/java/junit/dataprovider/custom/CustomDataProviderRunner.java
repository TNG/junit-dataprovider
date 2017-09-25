package com.tngtech.test.java.junit.dataprovider.custom;

import org.junit.runners.model.InitializationError;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;

public class CustomDataProviderRunner extends DataProviderRunner {

    public CustomDataProviderRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected void initializeHelpers() {
        super.initializeHelpers();
        dataConverter.setStringConverter(new DateTimeAwareStringConverter());
    }
}
