package com.tngtech.java.junit.dataprovider.internal.convert;

public class SingleArgConverter extends AbstractObjectConverter<Object> {

    /**
     * {@inheritDoc}
     *
     * @param data argument for test method
     * @param isVarArgs determines whether test method has a varargs parameter
     * @param parameterTypes target types of parameters
     * @return {@code Object[]} which is converted and checked
     * @throws IllegalArgumentException iif the paramter size does not fit, this is tried on varargs method or there is
     *             a type mismatch
     */
    @Override
    public Object[] convert(Object data, boolean isVarArgs, Class<?>[] parameterTypes) {
        if (parameterTypes.length != 1) {
            throw new IllegalArgumentException(String.format(
                    "Object[] dataprovider just supports single argument test method but found %d parameters",
                    parameterTypes.length));
        }
        if (isVarArgs) {
            throw new IllegalArgumentException(
                    "Object[] dataprovider and single argumented test method does not support varargs");
        }

        Object[] result = new Object[] { data };
        checkIfArgumentsMatchParameterTypes(result, parameterTypes);
        return result;
    }
}
