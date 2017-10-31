package com.tngtech.test.junit.dataprovider.custom.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@DataProviderTest(location = DataProviderLocation.class)
@interface FixedLocationDataProviderTest {
    // annotation needs no properties
}