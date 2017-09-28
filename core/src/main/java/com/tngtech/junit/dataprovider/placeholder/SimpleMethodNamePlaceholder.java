package com.tngtech.junit.dataprovider.placeholder;

public class SimpleMethodNamePlaceholder extends BasePlaceholder {
    public SimpleMethodNamePlaceholder() {
        super("%m");
    }

    @Override
    protected String getReplacementFor(String placeholder, ReplacementData data) {
        return data.getTestMethod().getName();
    }
}
