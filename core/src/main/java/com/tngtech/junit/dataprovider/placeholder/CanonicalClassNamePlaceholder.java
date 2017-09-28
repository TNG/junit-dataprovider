package com.tngtech.junit.dataprovider.placeholder;

public class CanonicalClassNamePlaceholder extends BasePlaceholder {
    public CanonicalClassNamePlaceholder() {
        super("%cc");
    }

    @Override
    protected String getReplacementFor(String placeholder, ReplacementData data) {
        return data.getTestMethod().getDeclaringClass().getCanonicalName();
    }
}
