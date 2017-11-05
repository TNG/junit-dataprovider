package com.tngtech.junit.dataprovider.convert;

import static com.tngtech.junit.dataprovider.Preconditions.checkArgument;

public class SingleArgConverter extends AbstractObjectConverter<Object> {

    /**
     * {@inheritDoc}
     *
     * @param data argument for test method
     * @param isVarargs determines whether test method has a varargs parameter
     * @param parameterTypes target types of parameters
     * @return {@code Object[]} which is converted and checked
     * @throws IllegalArgumentException if and only if the parameter size does not fit, test method has varargs, or
     *             there is a type mismatch
     */
    @Override
    public Object[] convert(Object data, boolean isVarargs, Class<?>[] parameterTypes) {
        checkArgument(parameterTypes.length >= 1,
                "Object[] dataprovider must at least have a single argument for the dataprovider but found no parameters");
        checkArgument(!isVarargs, "Object[] dataprovider does not support varargs");

        Object[] result = new Object[] { data };
        checkIfArgumentsMatchParameterTypes(result, parameterTypes);
        return result;
    }
}
