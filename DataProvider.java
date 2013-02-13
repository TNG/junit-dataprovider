package com.tngtech.java.junit.dataprovider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as a data provider used by a test method. The name of the data provider is the the name of
 * the method. The method must return an <code>Object[][]</code>. The test method will be called with each
 * "row" of this two-dimensonal array. The test method must be annotated with {@link UseDataProvider}. This
 * annotation behaves pretty much the same as the @DataProvider annotation from TestNG.<br/>
 * <b>Note:</b> The name of the test method in the junit result will be the name of the test method (annotated
 * by {@link UseDataProvider}) suffixed by the parameters. The last parameter is assumed to be the expected
 * value and will not be printed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataProvider {
    boolean expectedParameter() default true;
}

