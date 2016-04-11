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

    /**
     * This is the default value for {@link #value()}. If used the dataprovider name is tried to be guessed (=
     * convention over configuration) in the in {@link #value()} described order.
     */
    String DEFAULT_VALUE = "<use_convention>";

    /**
     * The optional name of the dataprovider method to use test data from. It is tried to be found like following:
     * <ul>
     * <li>Explicitly configured name of @{@link DataProvider} to be used (no fallback if dataprovider could not be
     * found)</li>
     * <li>@{@link DataProvider} annotated method which name equals the test method name</li>
     * <li>@{@link DataProvider} annotated method whereby prefix is replaced one out of the following table:
     * <table border="1" summary="Prefix replacement overview.">
     * <tr>
     * <th>prefix</th>
     * <th>replacement</th>
     * </tr>
     * <tr>
     * <td>test</td>
     * <td>dataProvider</td>
     * </tr>
     * <tr>
     * <td>test</td>
     * <td>data</td>
     * </tr>
     * </table>
     * </li>
     * <li>@{@link DataProvider} annotated method whereby additional prefix "dataProvider" or "data" is given. Also the
     * first letter of the original test method name is uppercased, e.g. {@code shouldReturnTwoForOnePlusOne} could
     * correspond to {@code dataProviderShouldReturnTwoForOnePlusOne}.</li>
     * </ul>
     * <b>Note</b>: As long as {@link #value()} contains the default value (= {@link #DEFAULT_VALUE}), it is tried to
     * guess the name of the dataprovider in the above described order.
     *
     * @return the name of the dataprovider method
     */
    String value() default DEFAULT_VALUE;

    /**
     * Optionally specify the class holding the dataprovider method having the name given in {@link #value()}. Defaults
     * to the test class where {@code @}{@link UseDataProvider} annotation is used. (Just first class will be
     * considered). Optional.
     *
     * @return the class holding the dataprovider method
     */
    Class<?>[] location() default {};
}
