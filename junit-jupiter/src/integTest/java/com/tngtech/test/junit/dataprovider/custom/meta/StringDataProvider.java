package com.tngtech.test.junit.dataprovider.custom.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.format.DataProviderPlaceholderFormatter;
import com.tngtech.junit.dataprovider.format.DataProviderTestNameFormatter;

/**
 * @see DataProvider
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@TestTemplate
@ExtendWith(StringDataProviderExtension.class)
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

    /**
     * @see DataProvider#format()
     */
    String format() default DataProvider.DEFAULT_FORMAT;

    /**
     * @see DataProvider#formatter()
     */
    Class<? extends DataProviderTestNameFormatter> formatter() default DataProviderPlaceholderFormatter.class;
}
