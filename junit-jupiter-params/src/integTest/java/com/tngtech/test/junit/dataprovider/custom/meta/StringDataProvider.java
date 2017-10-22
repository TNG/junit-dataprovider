package com.tngtech.test.junit.dataprovider.custom.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import com.tngtech.junit.dataprovider.DataProvider;

/**
 * @see DataProvider
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ParameterizedTest
@ArgumentsSource(StringDataProviderProvider.class)
@interface StringDataProvider {
    /**
     * @see DataProvider#value()
     */
    String[] value() default {};

    /**
     * @see DataProvider#splitBy()
     */
    String splitBy() default "\\|";

    /**
     * @see DataProvider#convertNulls()
     */
    boolean convertNulls() default true;

    /**
     * @see DataProvider#trimValues()
     */
    boolean trimValues() default true;

    /**
     * @see DataProvider#ignoreEnumCase()
     */
    boolean ignoreEnumCase() default false;
}
