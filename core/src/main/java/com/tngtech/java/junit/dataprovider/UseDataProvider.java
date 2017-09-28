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

    public enum ResolveStrategy {
        /**
         * Tries to find valid dataprovider methods looping over the provided {@link UseDataProvider#resolver()} until the first non-empty
         * result is returned or no further resolver is available.
         */
        UNTIL_FIRST_MATCH,

        /**
         * Loops over every provided {@link UseDataProvider#resolver()} and aggregates all resulting dataprovider methods. The test methods
         * to be executed are generated upon all these dataprovider methods.
         */
        AGGREGATE_ALL_MATCHES,
    }

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

    /**
     * @return strategy how to resolve the dataprovider methods which corresponds to the test method where this annotation is applied.
     * @see ResolveStrategy
     */
    ResolveStrategy resolveStrategy() default ResolveStrategy.UNTIL_FIRST_MATCH;
}
