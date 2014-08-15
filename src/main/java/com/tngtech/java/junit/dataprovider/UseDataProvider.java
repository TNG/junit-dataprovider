package com.tngtech.java.junit.dataprovider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a test method for using it with a dataprovider. The {@link #value()} must be the name of a {@code @}
 * {@link DataProvider} method which can optionally be located in another class (see {@link #location()}).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UseDataProvider {

    /** The required name of the dataprovider method to use test data from. */
    String value();

    /**
     * Optionally specify the class holding the dataprovider method having the name given in {@link #value()}. Defaults
     * to the test class where {@code @}{@link UseDataProvider} annotation is used. (Just first class will be
     * considered).
     */
    Class<?>[] location() default {};
}
