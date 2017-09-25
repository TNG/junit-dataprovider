package com.tngtech.java.junit.dataprovider.internal.placeholder;

public class IndexPlaceholder extends BasePlaceholder {
    public IndexPlaceholder() {
        super("%i");
    }

    @Override
    protected String getReplacementFor(String placeholder) {
        return String.valueOf(idx);
    }
}
