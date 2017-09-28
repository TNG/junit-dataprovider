package com.tngtech.junit.dataprovider.placeholder;

public class IndexPlaceholder extends BasePlaceholder {
    public IndexPlaceholder() {
        super("%i");
    }

    @Override
    protected String getReplacementFor(String placeholder, ReplacementData data) {
        return String.valueOf(data.getTestIndex());
    }
}
