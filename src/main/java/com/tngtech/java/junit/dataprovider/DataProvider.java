package com.tngtech.java.junit.dataprovider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Mark a method as a data provider used by a test method or use it directly at the test method and provide data via
 * {@link #value()} attribute.
 * <ul>
 * <li><i>Use it on a separate method:</i> The name of the data provider is the the name of the method. The method must
 * be static and return an {@link Object}{@code [][]}, a {@link List}{@code <List<Object>>}, or a {@link String}
 * {@code []}. The test method will be called with each "row" of this two-dimensional array. The test method must be
 * annotated with {@code @}{@link UseDataProvider}. This annotation behaves pretty much the same as the
 * {@code @DataProvider} annotation from <a href="http://testng.org/">TestNG</a>.
 * <p>
 * <b>Note:</b> The name of the test method in the JUnit result will be the name of the test method (annotated by
 * {@code @}{@link UseDataProvider}) suffixed by the parameters.</li>
 * <li>
 * <p>
 * <i>Use it directly on test method:</i> Provide all the data for the test method parameters as regex-separated
 * {@link String}s using {@code String[] value()}.
 * <p>
 * <b>Note:</b> All parameters of the test method must be primitive types (e.g. {@code char}, {@code int},
 * {@code double}), primitive wrapper types (e.g. {@link Boolean}, {@link Long}), case-sensitive {@link Enum} values,
 * {@link String}s, or types having single-argument {@link String} constructor. The former two are converted using the
 * {@code valueOf(String)} methods of their corresponding wrapper classes or
 * {@code valueOf(Class<? extends Enum<?>>, String)}, respectively. This can cause {@link Exception}s at runtime. A
 * {@link String} must not contain commas! The {@link String} "null" will always be passed as {@code null}.</li>
 * </ul>
 * <p>
 * If the test method arguments are retrieved from a regex-separated {@link String}{@code []}, the additional annotation
 * parameters can be used to customized the generation/conversion behavior.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataProvider {

    /** Define a list of parameters each as a regex-separated {@link String} for the annotated test method. */
    String[] value() default {};

    /**
     * The delimiting regular expression by which the regex-separated {@link String}s given by {@link #value()} or
     * returned by the method annotated with {@code @}{@link DataProvider} are split.
     *
     * @see String#split(String)
     */
    String splitBy() default ",";

    /**
     * Determines if every "null"-{@link String} in {@link #value()} or returned by the method annotated with {@code @}
     * {@link DataProvider} should be converted to {@code null} (= {@code true} ) or used as {@link String} (=
     * {@code false}).
     */
    boolean convertNulls() default true;

    /**
     * {@code true} if leading and trailing whitespace should be omitted in split {@link String}s given by
     * {@link #value()} or returned by the method annotated with {@code @}{@link DataProvider}, {@code false} otherwise.
     *
     * @see String#trim()
     */
    boolean trimValues() default true;
}
