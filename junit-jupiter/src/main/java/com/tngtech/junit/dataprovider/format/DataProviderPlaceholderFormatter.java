package com.tngtech.junit.dataprovider.format;

import java.lang.reflect.Method;
import java.util.List;

import com.tngtech.junit.dataprovider.placeholder.BasePlaceholder;
import com.tngtech.junit.dataprovider.placeholder.ReplacementData;

public class DataProviderPlaceholderFormatter implements DataProviderTestNameFormatter {

    private final String format;
    private final List<? extends BasePlaceholder> placeholders;

    public DataProviderPlaceholderFormatter(String format, List<? extends BasePlaceholder> placeholders) {
        this.format = format;
        this.placeholders = placeholders;
    }

    @Override
    public String format(Method testMethod, int invocationIndex, List<Object> arguments) {
        ReplacementData data = ReplacementData.of(testMethod, invocationIndex, arguments);

        String result = format;
        for (BasePlaceholder placeHolder : placeholders) {
            result = placeHolder.process(data, result);
        }
        return result;
    }
}
