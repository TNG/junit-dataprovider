package com.tngtech.java.junit.dataprovider.internal.convert;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS", justification = "for backwards compatibility and easier migration to core")
public class SingleArgConverter extends com.tngtech.junit.dataprovider.convert.SingleArgConverter {

    @Override
    public Object[] convert(Object data, boolean isVarArgs, Class<?>[] parameterTypes) {
        return super.convert(data, isVarArgs, parameterTypes);
    }
}
