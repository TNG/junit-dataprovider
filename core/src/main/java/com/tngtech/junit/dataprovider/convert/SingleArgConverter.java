package com.tngtech.junit.dataprovider.convert;

import static com.tngtech.junit.dataprovider.Preconditions.checkArgument;

public class SingleArgConverter extends AbstractObjectConverter<Object> {

    /**
     * {@inheritDoc}
     *
     * @param data argument for test method
     * @param isVarArgs determines whether test method has a varargs parameter
     * @param parameterTypes target types of parameters
     * @return {@code Object[]} which is converted and checked
     * @throws IllegalArgumentException if and only if the parameter size does not fit, this is tried on varargs method
     *             or there is a type mismatch
     */
    @Override
    public Object[] convert(Object data, boolean isVarArgs, Class<?>[] parameterTypes) {
        checkArgument(parameterTypes.length == 1, "Object[] dataprovider just supports single argument test method but found %d parameters",
                parameterTypes.length);
        checkArgument(!isVarArgs, "Object[] dataprovider and single parameter test method does not support varargs");

        Object[] result = new Object[] { data };
        checkIfArgumentsMatchParameterTypes(result, parameterTypes);
        return result;
    }
}
