package com.tngtech.test.junit.dataprovider.custom.converter;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.params.provider.ArgumentsSource;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.convert.ObjectArrayConverter;
import com.tngtech.junit.dataprovider.convert.SingleArgConverter;
import com.tngtech.junit.dataprovider.convert.StringConverter;

/**
 * @see DataProvider
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@ArgumentsSource(CustomConverterDataProviderArgumentProvider.class)
@interface CustomConverterDataProvider {

    /**
     * @see DataProvider#value()
     */
    String[] value() default {};

    /**
     * @see DataProvider#splitBy()
     */
    String splitBy() default DataProvider.COMMA;

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
     * @see DataProvider#objectArrayConverter()
     */
    Class<? extends ObjectArrayConverter> objectArrayConverter() default ObjectArrayConverter.class;

    /**
     * @see DataProvider#singleArgConverter()
     */
    Class<? extends SingleArgConverter> singleArgConverter() default SingleArgConverter.class;

    /**
     * @see DataProvider#stringConverter()
     */
    Class<? extends StringConverter> stringConverter() default DateTimeAwareStringConverter.class;
}
