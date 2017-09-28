package com.tngtech.junit.dataprovider.placeholder;

public class CompleteMethodSignaturePlaceholder extends BasePlaceholder {
    public CompleteMethodSignaturePlaceholder() {
        super("%cm");
    }

    @Override
    protected String getReplacementFor(String placeholder, ReplacementData data) {
        return data.getTestMethod().toString();
    }
}
