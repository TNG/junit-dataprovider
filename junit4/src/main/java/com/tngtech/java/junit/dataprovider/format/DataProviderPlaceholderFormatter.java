package com.tngtech.java.junit.dataprovider.format;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tngtech.java.junit.dataprovider.Placeholders;
import com.tngtech.junit.dataprovider.format.DataProviderTestNameFormatter;
import com.tngtech.junit.dataprovider.placeholder.BasePlaceholder;
import com.tngtech.junit.dataprovider.placeholder.ReplacementData;

public class DataProviderPlaceholderFormatter implements DataProviderTestNameFormatter {

    private final String nameFormat;
    private final List<? extends BasePlaceholder> placeholders;

    /**
     * @param nameFormat for formatting test name using placeholders, not {@code null}
     * @param additionalPlaceholders which should be added to beginning of the list of default placeholders, not
     *            {@code null}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public DataProviderPlaceholderFormatter(String nameFormat, BasePlaceholder... additionalPlaceholders) {
        this.nameFormat = checkNotNull(nameFormat, "'nameFormat' must not be null");
        checkNotNull(additionalPlaceholders, "'additionalPlaceholders' must not be null");

        List list = new ArrayList();
        for (BasePlaceholder placeholder : additionalPlaceholders) {
            list.add(placeholder);
        }
        list.addAll(Placeholders.all());
        this.placeholders = Collections.unmodifiableList(list);
    }

    /**
     * @param nameFormat for formatting test name using placeholders, not {@code null}
     * @param placeholders to be set as list of default placeholders, not {@code null}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public DataProviderPlaceholderFormatter(String nameFormat, List<? extends BasePlaceholder> placeholders) {
        this.nameFormat = checkNotNull(nameFormat, "'nameFormat' must not be null");
        this.placeholders = new ArrayList(checkNotNull(placeholders, "'placeholders' must not be null"));
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
