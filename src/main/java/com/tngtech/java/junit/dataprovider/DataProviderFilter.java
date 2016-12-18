package com.tngtech.java.junit.dataprovider;

import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

/**
 * This custom {@link Filter} filters test methods or even single dataprovider rows of a test class which is run by
 * {@link DataProviderRunner}.
 */
public class DataProviderFilter extends Filter {

    /**
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    static final Pattern DESCRIPTION_PATTERN = Pattern.compile("([^\\[\\] ]+)" + "(\\[(\\d+):.*\\])?" + "\\((.+)\\)$",
            Pattern.DOTALL);

    /**
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    static final Pattern GENEROUS_DESCRIPTION_PATTERN = Pattern.compile(
            "(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}+)" + "((.*))"
                    + "\\("
                        + "((\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)*"
                            + "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)"
                    + "\\)$", Pattern.DOTALL);

    private static final int GROUP_METHOD_NAME = 1;
    private static final int GROUP_METHOD_PARAMS = 2;
    private static final int GROUP_METHOD_IDX = 3;
    private static final int GROUP_CLASS = 4;

    /**
     * Original filter which is used if its description (= {@link Filter#describe()}) is not parsable by
     * {@link #DESCRIPTION_PATTERN}
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     **/
    final Filter filter;

    /**
     * Creates a new {@link DataProviderFilter} using the textual {@link Filter#describe()} of supplied {@link Filter}
     * to determine if a test method should run or not. If given {@code filter} description can not be parsed, request
     * for {@link #shouldRun(Description)} are just forwarded to it.
     *
     * @param filter from which the {@link Description} is parsed and used for filtering
     */
    public DataProviderFilter(Filter filter) {
        this.filter = checkNotNull(filter, "supplied filter must not be null");
    }

    @Override
    public boolean shouldRun(Description description) {
        Matcher filterDescriptionMatcher = DESCRIPTION_PATTERN.matcher(filter.describe());
        if (!filterDescriptionMatcher.find()) {
            return filter.shouldRun(description);
        }
        String methodName = filterDescriptionMatcher.group(GROUP_METHOD_NAME);
        String className = filterDescriptionMatcher.group(GROUP_CLASS);

        if (description.isTest()) {
            return shouldRunTest(description, filterDescriptionMatcher, methodName, className);
        }

        // explicitly check if any children should to run
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

    private boolean shouldRunTest(Description description, Matcher filterDescriptionMatcher, String methodName, String className) {
        Matcher descriptionMatcher = DESCRIPTION_PATTERN.matcher(description.getDisplayName());
        if (!descriptionMatcher.matches()) {
            if (filterDescriptionMatcher.group(GROUP_METHOD_IDX) == null) {
                Matcher generousDescMatcher = GENEROUS_DESCRIPTION_PATTERN.matcher(description.getDisplayName());
                if (generousDescMatcher.matches()) {
                    return methodName.equals(generousDescMatcher.group(GROUP_METHOD_NAME))
                            && className.equals(generousDescMatcher.group(GROUP_CLASS));
                }
            }
            return filter.shouldRun(description);
        }
        if (!methodName.equals(descriptionMatcher.group(GROUP_METHOD_NAME)) || !className.equals(descriptionMatcher.group(GROUP_CLASS))) {
            return false;
        }
        return filterDescriptionMatcher.group(GROUP_METHOD_PARAMS) == null
                || filterDescriptionMatcher.group(GROUP_METHOD_IDX).equals(descriptionMatcher.group(GROUP_METHOD_IDX));
    }
}