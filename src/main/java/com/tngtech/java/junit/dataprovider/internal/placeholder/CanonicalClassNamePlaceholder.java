package com.tngtech.java.junit.dataprovider.internal.placeholder;

public class CanonicalClassNamePlaceholder extends BasePlaceholder {
    public CanonicalClassNamePlaceholder() {
        super("%cc");
    }

    @Override
    protected String getReplacementFor(String placeholder) {
        return method.getDeclaringClass().getCanonicalName();
    }
}
