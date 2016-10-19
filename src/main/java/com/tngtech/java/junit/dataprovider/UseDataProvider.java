package com.tngtech.java.junit.dataprovider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tngtech.java.junit.dataprovider.internal.DefaultDataProviderMethodResolver;

/**
 * Annotate a test method for using it with a dataprovider. The {@link #resolver()} is used to find a proper dataprovider.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UseDataProvider {

    /**
     * This is the default value for {@link #value()}. If used the dataprovider name is tried to be guessed (= convention over
     * configuration) in the in {@link #value()} described order.
     */
    String DEFAULT_VALUE = "<use_convention>";

    /**
     * A value to derive the dataprovider method from. In which way depends on the given {@link #resolver()}. Defaults to
     * {@link #DEFAULT_VALUE}.
     *
     * @return a value from which the dataprovider method can be derived
     */
    String value() default DEFAULT_VALUE;

    /**
     * One or multiple locations where the {@link DataProviderMethodResolver} can look out for a proper dataprovider method. It depends on
     * the provided {@link #resolver()} how this is used.
     *
     * @return a array of {@link Class}es which could be used to derive the dataprovider method
     */
    Class<?>[] location() default {};

    /**
     * The resolvers used to derive the dataprovider method from. It is tried until the first resolver returns a proper dataprovider method
     * (= not {@code null}) or no more resolvers are available.
     *
     * @return the resolver which are used to derive the dataprovider method
     */
    Class<? extends DataProviderMethodResolver>[] resolver() default { DefaultDataProviderMethodResolver.class };
}
