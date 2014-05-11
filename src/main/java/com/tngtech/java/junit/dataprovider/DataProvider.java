package com.tngtech.java.junit.dataprovider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as a data provider used by a test method or use it directly at the test method and provide data via
 * {@link #value()} attribute.
 * <ul>
 * <li><i>Use it on a separate method:</i> The name of the data provider is the the name of the method. The method must
 * be static and return an {@link Object} {@code [][]}. The test method will be called with each "row" of this
 * two-dimensional array. The test method must be annotated with {@code @}{@link UseDataProvider}. This annotation
 * behaves pretty much the same as the {@code @DataProvider} annotation from <a href="http://testng.org/">TestNG</a>.
 * <p>
 * <b>Note:</b> The name of the test method in the JUnit result will be the name of the test method (annotated by
 * {@code @}{@link UseDataProvider}) suffixed by the parameters. The last parameter is assumed to be the expected value
 * and will not be printed.</li>
 * <li>
 * <p>
 * <i>Use it directly on test method:</i> Provide all the data for the test method parameters as comma-separted
 * {@link String}s using {@code String[] value()}.
 * <p>
 * <b>Note:</b> All parameters of the test method must be primitive types (e.g. {@code char}, {@code int},
 * {@code double}), primitive wrapper types (e.g. {@link Boolean}, {@link Long}), case-sensitive {@link Enum} values, or
 * {@link String}s. The former two are converted using the {@code valueOf(String)} methods of their corresponding
 * wrapper classes or {@code valueOf(Class<? extends Enum<?>>, String)}, respectively. This can cause {@link Exception}s
 * at runtime. Latter must not contain commas! The {@link String} "null" will always be passed as {@code null}.</li>
 * </ul>
 * <p>
 * Copyright by TNG Technology Consulting GmbH, Germany
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataProvider {

    /** Use this Define a list of parameters for the test method */
    String[] value() default {};
}
