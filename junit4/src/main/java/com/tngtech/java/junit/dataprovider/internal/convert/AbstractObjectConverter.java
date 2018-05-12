package com.tngtech.java.junit.dataprovider.internal.convert;

import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkArgument;
import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkNotNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @deprecated Use {@link com.tngtech.junit.dataprovider.convert.AbstractObjectConverter} instead. Public API kept the
 *             same. This class will be removed in version 3.0.
 */
@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS", justification = "for backwards compatibility and easier migration to core")
@Deprecated
public abstract class AbstractObjectConverter<V>
        extends com.tngtech.junit.dataprovider.convert.AbstractObjectConverter<V> {

    @Override
    public abstract Object[] convert(V data, boolean isVarargs, Class<?>[] parameterTypes);

    @Override
    protected void checkIfArgumentsMatchParameterTypes(Object[] arguments, Class<?>[] parameterTypes) {
        checkNotNull(arguments, "'arguments' must not be null");
        checkNotNull(parameterTypes, "'testMethod' must not be null");
        checkArgument(parameterTypes.length == arguments.length, "Expected %d arguments for test method but got %d.",
                parameterTypes.length, arguments.length);

        super.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);
    }
}
