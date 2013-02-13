package com.tngtech.java.junit.dataprovider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a test method for use with a dataprovider. The value must be the name of a {@link DataProvider}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UseDataProvider {
    /** the name of the data provider method */
    String value();
}

