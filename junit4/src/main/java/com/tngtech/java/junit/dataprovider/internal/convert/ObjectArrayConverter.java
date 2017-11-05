package com.tngtech.java.junit.dataprovider.internal.convert;

import static com.tngtech.junit.dataprovider.Preconditions.checkArgument;
import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS", justification = "for backwards compatibility and easier migration to core")
public class ObjectArrayConverter extends com.tngtech.junit.dataprovider.convert.ObjectArrayConverter {

    @Override
    public Object[] convert(Object[] data, boolean isVarArgs, Class<?>[] parameterTypes) {
        return super.convert(data, isVarArgs, parameterTypes);
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
