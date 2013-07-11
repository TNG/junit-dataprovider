package com.tngtech.java.junit.dataprovider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

/**
 * This custom {@link Filter} filters test methods or even single data provider rows of a test class which is run by
 * {@link DataProviderRunner}.
 */
public class DataProviderFilter extends Filter {

    /**
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    static final Pattern DESCRIPTION_PATTERN = Pattern.compile("([^\\[\\] ]+)" + "(\\[(\\d+):.*\\])?" + "\\((.+)\\)$");

    private static final int GROUP_METHOD_NAME = 1;
    private static final int GROUP_METHOD_PARAMS = 2;
    private static final int GROUP_METHOD_IDX = 3;
    private static final int GROUP_CLASS = 4;

    private final Filter filter;
    private final Matcher filterDescriptionMatcher;

    /**
     * Creates a new {@link DataProvider} using the textual {@link Filter#describe()} of supplied {@link Filter} to
     * determine if a test method should run or not.
     *
     * @throws IllegalArgumentException if supplied {@link Filter} is {@code null} or
     *             {@link Description#getDisplayName()} of supplied {@link Description} cannot be parsed
     */
    public DataProviderFilter(Filter filter) {
        if (filter == null) {
            throw new NullPointerException("supplied filter must not be null");
        }
        this.filter = filter;

        filterDescriptionMatcher = DESCRIPTION_PATTERN.matcher(filter.describe());
        if (!filterDescriptionMatcher.find()) {
            throw new IllegalArgumentException(String.format("Filter %s with description %s is not supported by %s.",
                    filter.getClass(), filter.describe(), this.getClass().getSimpleName()));
        }
    }

    /**
     * @throws IllegalArgumentException if {@link Description#getDisplayName()} of supplied {@link Description} cannot
     *             be parsed
     */
    @Override
    public boolean shouldRun(Description description) {
        if (description.isTest()) {
            Matcher descriptionMatcher = DESCRIPTION_PATTERN.matcher(description.getDisplayName());
            if (!descriptionMatcher.matches()) {
                throw new IllegalArgumentException(String.format("Test method description %s is not suppored by %s.",
                        filter.describe(), this.getClass().getSimpleName()));
            }
            if (!filterDescriptionMatcher.group(GROUP_METHOD_NAME).equals(descriptionMatcher.group(GROUP_METHOD_NAME))
                    || !filterDescriptionMatcher.group(GROUP_CLASS).equals(descriptionMatcher.group(GROUP_CLASS))) {

                return false;
            }
            return filterDescriptionMatcher.group(GROUP_METHOD_PARAMS) == null
                    || filterDescriptionMatcher.group(GROUP_METHOD_IDX).equals(
                            descriptionMatcher.group(GROUP_METHOD_IDX));
        }

        // explicitly check if any children want to run
        for (Description each : description.getChildren()) {
            if (shouldRun(each)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String describe() {
        return filter.describe();
    }
}
