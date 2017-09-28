package com.tngtech.java.junit.dataprovider.internal.placeholder;

public class SimpleMethodNamePlaceholder extends BasePlaceholder {
    public SimpleMethodNamePlaceholder() {
        super("%m");
    }

    @Override
    protected String getReplacementFor(String placeholder) {
        return method.getName();
    }
}
