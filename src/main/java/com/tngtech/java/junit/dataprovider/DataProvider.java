package com.tngtech.java.junit.dataprovider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as a data provider used by a test method. The name of the data provider is the the name of the method.
 * The method must be static and return an {@link Object}{@code [][]}. The test method will be called with each "row" of
 * this two-dimensional array. The test method must be annotated with {@code @}{@link UseDataProvider}. This annotation
 * behaves pretty much the same as the {@code @DataProvider} annotation from <a href="http://testng.org/">TestNG</a>.
 * <p>
 * <b>Note:</b> The name of the test method in the junit result will be the name of the test method (annotated by
 * {@code @}{@link UseDataProvider}) suffixed by the parameters. The last parameter is assumed to be the expected value
 * and will not be printed.
 * </p>
 * <p>
 * Copyright by TNG Technology Consulting GmbH, Germany
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataProvider {
    // has no attributes
}
