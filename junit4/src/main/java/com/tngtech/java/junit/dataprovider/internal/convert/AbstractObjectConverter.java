package com.tngtech.java.junit.dataprovider.internal.convert;

public abstract class AbstractObjectConverter<V>
        extends com.tngtech.junit.dataprovider.convert.AbstractObjectConverter<V> {

    @Override
    public abstract Object[] convert(V data, boolean isVarArgs, Class<?>[] parameterTypes);

    @Override
    protected void checkIfArgumentsMatchParameterTypes(Object[] arguments, Class<?>[] parameterTypes) {
        super.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);
    }
}
