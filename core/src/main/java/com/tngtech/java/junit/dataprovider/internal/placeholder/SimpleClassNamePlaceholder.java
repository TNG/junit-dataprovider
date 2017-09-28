package com.tngtech.java.junit.dataprovider.internal.placeholder;

public class SimpleClassNamePlaceholder extends BasePlaceholder {
    public SimpleClassNamePlaceholder() {
        super("%c");
    }

    @Override
    protected String getReplacementFor(String placeholder) {
        return method.getDeclaringClass().getSimpleName();
    }
}
