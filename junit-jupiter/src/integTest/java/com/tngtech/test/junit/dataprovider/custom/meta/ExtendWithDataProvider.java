package com.tngtech.test.junit.dataprovider.custom.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProviderExtension;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@ExtendWith(DataProviderExtension.class)
@ExtendWith(UseDataProviderExtension.class)
@interface ExtendWithDataProvider {
    // meta annotation for all possibilites of using a dataprovider
}
