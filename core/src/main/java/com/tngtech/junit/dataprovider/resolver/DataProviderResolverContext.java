package com.tngtech.junit.dataprovider.resolver;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Context required for resolving dataproviders in {@link DataProviderMethodResolverHelper} and
 * {@link DataProviderMethodResolver}.
 */
public class DataProviderResolverContext {

    /**
     * Putting this method name as {@code dataProviderName} implies that the {@link DataProviderMethodResolver} should
     * apply the defined conventions to resolve the dataproviders method name.
     */
    public static final String METHOD_NAME_TO_USE_CONVENTION = "<use_convention>";

    private final Method testMethod;

    private final List<Class<? extends DataProviderMethodResolver>> resolverClasses;
    private final ResolveStrategy resolveStrategy;

    private final List<Class<?>> locations;
    private final Class<? extends Annotation> dataProviderAnnotationClass;
    private final String dataProviderName;

    /**
     * @param testMethod for which a dataprovider should be resolved
     * @param resolverClasses to use for resolving dataproviders
     * @param resolveStrategy used to determine if only first match or all matches should be returned
     * @param locations where to search for potential dataproviders; if empty, test methods declaring class will be used
     * @param dataProviderAnnotationClass used to identify methods as dataproviders
     * @param dataProviderName used to find matching dataproviders or {@link #METHOD_NAME_TO_USE_CONVENTION} if a method
     *            name should be derived via convention
     * @throws NullPointerException if one of the given arguments is {@code null}
     *
     */
    public DataProviderResolverContext(Method testMethod,
            List<Class<? extends DataProviderMethodResolver>> resolverClasses, ResolveStrategy resolveStrategy,
            List<Class<?>> locations, Class<? extends Annotation> dataProviderAnnotationClass,
            String dataProviderName) {
        this.testMethod = checkNotNull(testMethod, "'testMethod' must not be null");
        this.resolverClasses = new ArrayList<Class<? extends DataProviderMethodResolver>>(
                checkNotNull(resolverClasses, "'resolverClasses' must not be null"));
        this.resolveStrategy = checkNotNull(resolveStrategy, "'resolveStrategy' must not be null");

        checkNotNull(locations, "'locations' must not be null");
        if (locations.isEmpty()) {
            this.locations = Collections.<Class<?>>singletonList(testMethod.getDeclaringClass());
        } else {
            this.locations = new ArrayList<Class<?>>(locations);
        }
        this.dataProviderAnnotationClass = checkNotNull(dataProviderAnnotationClass,
                "'dataProviderAnnotationClass' must not be null");
        this.dataProviderName = checkNotNull(dataProviderName, "'dataProviderName' must not be null");
    }

    /**
     * @return {@code true} if and only if dataprovider name should be derived by convention
     */
    public boolean useDataProviderNameConvention() {
        return METHOD_NAME_TO_USE_CONVENTION.equals(dataProviderName);
    }

    /**
     * @return test method for which a dataprovider should be resolved
     */
    public Method getTestMethod() {
        return testMethod;
    }

    /**
     * @return resolver classes to be used for resolving dataproviders
     */
    public List<Class<? extends DataProviderMethodResolver>> getResolverClasses() {
        return unmodifiableList(resolverClasses);
    }

    /**
     * @return {@link ResolveStrategy} used to determine if only first match or all matches should be returned
     */
    public ResolveStrategy getResolveStrategy() {
        return resolveStrategy;
    }

    /**
     * @return locations where to search for potential dataproviders. Defaults to declaring class of {@code testMethod}
     */
    public List<Class<?>> getLocations() {
        return unmodifiableList(locations);
    }

    /**
     * @return the annotation class of the dataprovider which is used to identify dataproviders
     */
    public Class<? extends Annotation> getDataProviderAnnotationClass() {
        return dataProviderAnnotationClass;
    }

    /**
     * @return the {@code dataProviderName} used to find matching dataproviders or
     *         {@link #METHOD_NAME_TO_USE_CONVENTION} if a method name should be derived via convention
     * @see #useDataProviderNameConvention()
     */
    public String getDataProviderName() {
        return dataProviderName;
    }
}
