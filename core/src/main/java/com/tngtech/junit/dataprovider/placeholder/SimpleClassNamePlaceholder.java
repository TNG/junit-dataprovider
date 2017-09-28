package com.tngtech.junit.dataprovider.placeholder;

public class SimpleClassNamePlaceholder extends BasePlaceholder {
    public SimpleClassNamePlaceholder() {
        super("%c");
    }

    @Override
    protected String getReplacementFor(String placeholder, ReplacementData data) {
        return data.getTestMethod().getDeclaringClass().getSimpleName();
    }
}
