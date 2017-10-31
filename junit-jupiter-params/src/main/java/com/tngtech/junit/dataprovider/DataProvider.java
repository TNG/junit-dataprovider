package com.tngtech.junit.dataprovider;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.params.provider.ArgumentsSource;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.convert.ObjectArrayConverter;
import com.tngtech.junit.dataprovider.convert.SingleArgConverter;
import com.tngtech.junit.dataprovider.convert.StringConverter;

/**
 * Mark a method as a dataprovider used by a test method or use it directly at the test method and provide data via {@link #value()}
 * attribute.
 * <ul>
 * <li><i>Use it on a separate method:</i> The name of the dataprovider is the the name of the method. The method must be static and return
 * an {@link Object}{@code [][]}, an {@link Iterable}{@code <Iterable<?>>}, an {@link Iterable}{@code <?>}, or a {@link String}{@code []}.
 * Whereby {@link Iterable} can be replaced with any vaild subtype as well as arbitrary inner types are also supported. The test method will
 * be called with each "row" of this two-dimensional array. The test method must be annotated with {@code @}{@link UseDataProvider}. This
 * annotation behaves pretty much the same as the {@code @DataProvider} annotation from <a href="http://testng.org/">TestNG</a>.
 * <li>
 * <p>
 * <i>Use it directly on test method:</i> Provide all the data for the test method parameters as regex-separated {@link String}s using
 * {@code String[] value()}.
 * <p>
 * <b>Note:</b> All parameters of the test method must be primitive types (e.g. {@code char}, {@code int}, {@code double}), primitive
 * wrapper types (e.g. {@link Boolean}, {@link Long}), case-sensitive {@link Enum} names, {@link String}s, or types having single-argument
 * {@link String} constructor. The former two are converted using the {@code valueOf(String)} methods of their corresponding wrapper classes
 * or {@code valueOf(Class<? extends Enum<?>>, String)}, respectively. This can cause {@link Exception}s at runtime. A {@link String} must
 * not contain commas! The {@link String} "null" will be passed as {@code null} or {@link String}, according to
 * {@link #convertNulls()}.</li>
 * </ul>
 * <p>
 * If the test method arguments are retrieved from a regex-separated {@link String}{@code []}, the additional annotation parameters can be
 * used to customized the generation/conversion behavior.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@ArgumentsSource(StringDataProviderArgumentProvider.class)
public @interface DataProvider {

    /**
     * Comma delimiter to split up arguments for dataproviders using the {@link #value()}s {@link String}
     * representation.
     *
     * @see #splitBy()
     */
    String COMMA = ConverterContext.COMMA;

    /**
     * {@code null}-{@link String} value to be converted to {@code null} if {@link #convertNulls()} is {@code true}.
     *
     * @see #convertNulls()
     */
    String NULL = ConverterContext.NULL;

    /**
     * Define a list of parameters each as a regex-separated {@link String} for the annotated test method. Optional.
     *
     * @return list of regex-separated {@link String} parameters
     */
    String[] value() default {};

    /**
     * The delimiting regular expression by which the regex-separated {@link String}s given by {@link #value()} or
     * returned by the method annotated with {@code @}{@link DataProvider} are split. Defaults to {@value #COMMA}.
     * Optional.
     *
     * @return the regex to split {@link String} data
     * @see String#split(String)
     * @see #COMMA
     */
    String splitBy() default COMMA;

    /**
     * Determines if every {@value #NULL}-{@link String} in {@link #value()} or returned by the method annotated with
     * {@code @}{@link DataProvider} should be converted to {@code null} (= {@code true} ) or used as {@link String} (=
     * {@code false}). Default is {@code true}. Optional.
     *
     * @return {@code true} iif "null"-{@link String}s should be converted to {@code null}.
     * @see #NULL
     */
    boolean convertNulls() default true;

    /**
     * {@code true} if leading and trailing whitespace should be omitted in split {@link String}s given by
     * {@link #value()} or returned by the method annotated with {@code @}{@link DataProvider}, {@code false} otherwise.
     * Default is {@code true}. Optional.
     *
     * @return {@code true} iif regex-separated {@link String} data should be trimmed
     * @see String#trim()
     */
    boolean trimValues() default true;

    /**
     * @return {@code true} if and only if the case for {@link Enum} conversion should be ignored such that searching
     *         for the corresponding {@link Enum} values is case-insensitive. Default is {@code false}. Optional.
     */
    boolean ignoreEnumCase() default false;

    /**
     * @return a custom converter converting {@link Object}{@code []} data to proper arguments
     * @see ObjectArrayConverter
     */
    Class<? extends ObjectArrayConverter> objectArrayConverter() default ObjectArrayConverter.class;

    /**
     * @return a custom converter converting {@link Object} to proper argument
     * @see SingleArgConverter
     */
    Class<? extends SingleArgConverter> singleArgConverter() default SingleArgConverter.class;

    /**
     * @return a custom converter converting a {@link String} to proper arguments by splitting and converting it to the
     *         corresponding parameter type
     * @see StringConverter
     */
    Class<? extends StringConverter> stringConverter() default StringConverter.class;
}
