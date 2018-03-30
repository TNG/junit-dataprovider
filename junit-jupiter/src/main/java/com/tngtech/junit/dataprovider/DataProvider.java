package com.tngtech.junit.dataprovider;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

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
 * <p>
 * <b>Note:</b> The name of the test method in the JUnit result will by default be the name of the test method (annotated by
 * {@code @}{@link UseDataProvider}) suffixed by the parameters, can be changed by customizing {@link #format()}.</li>
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
     * Default format string containing test method name followed by an index and all parameters within square brackets.
     *
     * @see #format()
     */
    String DEFAULT_FORMAT = "[%i: %a[0..-1]]";

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
     * @return {@code true} if and only if the result of this dataprovider should be cached. Otherwise it will be
     *         evaluated for every usage. This could be necessary if it contains dynamic results, e.g. depending on a
     *         provided parameter. Default is {@code true}.
     */
    boolean cache() default true;

    /**
     * Format pattern to be used to generate test method description. The following placeholders are by default
     * available (for more information see their implementations in package
     * {@link com.tngtech.junit.dataprovider.placeholder}:
     * <table border="1" summary="Possible placeholders">
     * <tr>
     * <th>Placeholder</th>
     * <th>Aliases</th>
     * <th>Example</th>
     * <th>Description</th>
     * </tr>
     * <tr>
     * <td>{@code %c}</td>
     * <td><i>DataProviderRunnerTest</i></td>
     * <td>Simple name of test method class (= {@link Class#getSimpleName()})</td>
     * </tr>
     * <tr>
     * <td>{@code %cc}</td>
     * <td><i>com.tngtech.java .junit.dataprovider .DataProviderRunnerTest</i></td>
     * <td>Canonical name of test method class (= {@link Class#getCanonicalName()})</td>
     * </tr>
     * <tr>
     * <td>{@code %m}</td>
     * <td><i>testIsEmptyString</i></td>
     * <td>Simple name of test method (= {@link Method#getName()})</td>
     * </tr>
     * <tr>
     * <td>{@code %cm}</td>
     * <td><i>com.tngtech.test .java.junit.dataprovider .DataProviderJavaAcceptanceTest
     * .testIsEmptyString(java.lang.String) </i></td>
     * <td>Complete signature of test method (= {@link Method#toString()})</td>
     * </tr>
     * <tr>
     * <td>{@code %i}</td>
     * <td><i>13</i></td>
     * <td>Index of the dataprovider test of current test method (starting at {@code 0}). Useful to generate unique test
     * method descriptions.</td>
     * </tr>
     * <tr>
     * <td>{@code %a[x]}</td>
     * <td><i>test, &lt;null&gt;, 4</i></td>
     * <td>Subscripting all parameters by positive or negative index (1.) and range (2.). All indices may either be
     * positive (starting at {@code 0} and increment) to number parameters from the beginning or negative (starting from
     * {@code -1} and decrement) to number parameters from the end:
     * <ol>
     * <li>A positive or negative index {code x} to get the {@link String} representation of a specific parameter, e.g.
     * {@code %a[3]} (= third parameter) or {@code %a[-2]} (= second last parameter).</li>
     * <li>A range index producing the comma-separated String representation of corresponding parameters, e.g.
     * <ul>
     * <li>{@code %a[0..1]} for the first two parameters,</li>
     * <li>{@code %a[0..-2]} for all parameters expect the last, or</li>
     * <li>{@code %a[-3..-1]} for the last two parameters.</li>
     * </ul>
     * </ol>
     * <b>Attention:</b> This placeholder will cause an {@link IndexOutOfBoundsException} iif any index exceeds the
     * parameters array length</td>
     * </tr>
     * <tr>
     * <td>{@code %na[x]}</td>
     * <td><i>string=test, search=e, index=1</i></td>
     * <td>Named arguments with same subscription options as {@code %a}.</td>
     * </tr>
     * <tr>
     * <td>{@code %p[x]}</td>
     * <td><i>test, &lt;null&gt;, 4</i></td>
     * <td><b>deprecated.</b> Use {@code %a} which has the same effect.</td>
     * </tr>
     * </table>
     * <b>Hints:</b>
     * <ul>
     * <li>A produced test method name should be unique among all other in the same class.</li>
     * <li>Every above listed parameter can be used multiple times</li>
     * </ul>
     * Defaults to {@value #DEFAULT_FORMAT}. Optional.
     *
     * @return the format pattern to generate test method description
     * @see #DEFAULT_FORMAT
     */
    String format() default DEFAULT_FORMAT;

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
