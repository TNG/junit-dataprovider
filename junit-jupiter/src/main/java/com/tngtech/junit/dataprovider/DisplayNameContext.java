package com.tngtech.junit.dataprovider;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.tngtech.junit.dataprovider.placeholder.BasePlaceholder;

public class DisplayNameContext {

    private final String format;
    private final List<? extends BasePlaceholder> placeholders;

    public DisplayNameContext(String format, List<? extends BasePlaceholder> placeholders) {
        this.format = checkNotNull(format, "'format' must not be null");
        this.placeholders = new ArrayList<>(checkNotNull(placeholders, "'placeholders' must not be null"));
    }

    public String getFormat() {
        return format;
    }

    public List<? extends BasePlaceholder> getPlaceholders() {
        return unmodifiableList(placeholders);
    }
}
