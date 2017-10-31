package com.tngtech.test.junit.dataprovider.custom.resolver;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.resolver.DataProviderMethodResolver;
import com.tngtech.junit.dataprovider.resolver.DataProviderResolverContext;
import com.tngtech.junit.dataprovider.resolver.ResolveStrategy;

/**
 * Annotate a test method for using it with a dataprovider.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@TestTemplate
@ExtendWith(CustomResolverDataProviderTestExtension.class)
@interface CustomResolverDataProviderTest {

    /**
     * @see UseDataProvider#value()
     */
    String value() default DataProviderResolverContext.METHOD_NAME_TO_USE_CONVENTION;

    /**
     * @see UseDataProvider#location()
     */
    Class<?>[] location() default {};

    /**
     * @see UseDataProvider#resolver()
     */
    Class<? extends DataProviderMethodResolver>[] resolver() default {
            DataProviderStartWithTestMethodNameResolver.class };

    /**
     * @see UseDataProvider#resolveStrategy()
     */
    ResolveStrategy resolveStrategy() default ResolveStrategy.UNTIL_FIRST_MATCH;
}
