package com.tngtech.java.junit.dataprovider.internal.convert;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS", justification = "for backwards compatibility and easier migration to core")
public abstract class AbstractObjectConverter<V>
        extends com.tngtech.junit.dataprovider.convert.AbstractObjectConverter<V> {

    @Override
    public abstract Object[] convert(V data, boolean isVarArgs, Class<?>[] parameterTypes);

    @Override
    protected void checkIfArgumentsMatchParameterTypes(Object[] arguments, Class<?>[] parameterTypes) {
        super.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);
    }
}
