package com.tngtech.java.junit.dataprovider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a test method for use with a data provider. The value must be the name of a {@code @}{@link DataProvider}
 * method.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UseDataProvider {

    /** The required name of the data provider method to use test data from. */
    String value();

    /** The class holding the data provider method, defaults to the test class (just first class will be considered). */
    Class<?>[] location() default {};
}
