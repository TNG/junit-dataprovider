package com.tngtech.java.junit.dataprovider.internal.placeholder;

public class CompleteMethodSignaturePlaceholder extends BasePlaceholder {
    public CompleteMethodSignaturePlaceholder() {
        super("%cm");
    }

    @Override
    protected String getReplacementFor(String placeholder) {
        return method.toString();
    }
}
