package com.tngtech.junit.dataprovider;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.params.provider.ArgumentsSource;

import com.tngtech.junit.dataprovider.resolver.DataProviderMethodResolver;
import com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext;
import com.tngtech.junit.dataprovider.resolver.DefaultDataProviderMethodResolver;
import com.tngtech.junit.dataprovider.resolver.ResolveStrategy;

/**
 * Annotate a test method for using it with a dataprovider.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@ArgumentsSource(UseDataProviderArgumentProvider.class)
public @interface UseDataProvider {

    /**
     * A value to derive the dataprovider method from. In which way depends on the given {@link #resolver()}. Defaults
     * to {@link DataProviderResolverContext#METHOD_NAME_TO_USE_CONVENTION}.
     *
     * @return a value from which the dataprovider method can be derived
     */
    String value() default DataProviderResolverContext.METHOD_NAME_TO_USE_CONVENTION;

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
