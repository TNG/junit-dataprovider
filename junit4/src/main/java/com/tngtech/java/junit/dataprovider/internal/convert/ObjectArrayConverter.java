package com.tngtech.java.junit.dataprovider.internal.convert;

import static com.tngtech.junit.dataprovider.Preconditions.checkArgument;
import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @deprecated Use {@link com.tngtech.junit.dataprovider.convert.ObjectArrayConverter} of {@code core/} instead. Public
 *             API kept the same. This class will be removed in version 3.0.
 */
@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS", justification = "for backwards compatibility and easier migration to core")
@Deprecated
public class ObjectArrayConverter extends com.tngtech.junit.dataprovider.convert.ObjectArrayConverter {

    @Override
    public Object[] convert(Object[] data, boolean isVarargs, Class<?>[] parameterTypes) {
        return super.convert(data, isVarargs, parameterTypes);
    }

    @Override
    protected void checkIfArgumentsMatchParameterTypes(Object[] arguments, Class<?>[] parameterTypes) {
        checkNotNull(arguments, "'arguments' must not be null");
        checkNotNull(parameterTypes, "'testMethod' must not be null");
        checkArgument(parameterTypes.length == arguments.length,
                "Expected %d arguments for test method but got %d parameters.", arguments.length,
                parameterTypes.length);
        super.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);
    }
}
