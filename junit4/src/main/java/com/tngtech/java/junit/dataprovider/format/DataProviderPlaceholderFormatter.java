package com.tngtech.java.junit.dataprovider.format;

import java.lang.reflect.Method;
import java.util.List;

import com.tngtech.junit.dataprovider.format.DataProviderTestNameFormatter;
import com.tngtech.junit.dataprovider.placeholder.BasePlaceholder;
import com.tngtech.junit.dataprovider.placeholder.ReplacementData;

public class DataProviderPlaceholderFormatter implements DataProviderTestNameFormatter {

    private final String nameFormat;
    private final List<? extends BasePlaceholder> placeholders;

    public DataProviderPlaceholderFormatter(String nameFormat, List<? extends BasePlaceholder> placeholders) {
        this.nameFormat = nameFormat;
        this.placeholders = placeholders;
    }

    @Override
    public String format(Method testMethod, int invocationIndex, List<Object> arguments) {
        String result = nameFormat;
        for (BasePlaceholder placeholder : placeholders) {
            if (placeholder instanceof com.tngtech.java.junit.dataprovider.internal.placeholder.BasePlaceholder) {
                com.tngtech.java.junit.dataprovider.internal.placeholder.BasePlaceholder castedPlaceholder = (com.tngtech.java.junit.dataprovider.internal.placeholder.BasePlaceholder) placeholder;
                synchronized (castedPlaceholder) {
                    castedPlaceholder.setContext(testMethod, invocationIndex, arguments.toArray());
                    result = castedPlaceholder.process(result);
                }

            } else {
                ReplacementData data = ReplacementData.of(testMethod, invocationIndex, arguments);
                result = placeholder.process(data, result);
            }
        }
        return result;
    }
}
