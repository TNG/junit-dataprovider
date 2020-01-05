package com.tngtech.test.junit.dataprovider.custom.resolver;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.params.provider.ArgumentsSource;

import com.tngtech.junit.dataprovider.resolver.DataProviderMethodResolver;
import com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext;
import com.tngtech.junit.dataprovider.resolver.ResolveStrategy;

/**
 * Annotate a test method for using it with a dataprovider.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@ArgumentsSource(CustomResolverUseDataProviderArgumentProvider.class)
@interface CustomResolverUseDataProvider {

    /**
     * @see com.tngtech.junit.dataprovider.UseDataProvider#value()
     */
    String value() default DataProviderResolverContext.METHOD_NAME_TO_USE_CONVENTION;

    /**
     * @see com.tngtech.junit.dataprovider.UseDataProvider#location()
     */
    Class<?>[] location() default {};

    /**
     * @see com.tngtech.junit.dataprovider.UseDataProvider#resolver()
     */
    Class<? extends DataProviderMethodResolver>[] resolver() default {
            DataProviderStartWithTestMethodNameResolver.class };

    /**
     * @see com.tngtech.junit.dataprovider.UseDataProvider#resolveStrategy()
     */
    ResolveStrategy resolveStrategy() default ResolveStrategy.UNTIL_FIRST_MATCH;
}
