package com.tngtech.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

public class DataProviderFilterTest {

    private DataProviderFilter underTest;

    private Filter filter;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = NullPointerException.class)
    public void testDataProviderFilterShouldThrowNullPointerExceptionWhenFilterIsNull() {

        // Given:

        // When:
        @SuppressWarnings("unused")
        DataProviderFilter result = new DataProviderFilter(null);

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDataProviderFilterShouldThrowIllegalArgumentExceptionWhenFilterDescriptionCannotBeParsed() {

        // Given:

        // When:
        setupDataProviderFilterWith("invalid");

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldRunShouldThrowIllegalArgumentExceptionWhenDescriptionCannotBeParsed() {

        // Given:
        setupDataProviderFilterWith("Method testMain[1: ](Clazz)");
        Description description = setupDescription(true, "invalid");

        // When:
        underTest.shouldRun(description);

        // Then: expect exception
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionDoesNotHaveExpectedMethodName() {

        // Given:
        setupDataProviderFilterWith("Method testMain[1: ](Clazz)");
        Description description = setupDescription(true, "testOther[1: ](Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionDoesNotHaveExpectedClassName() {

        // Given:
        setupDataProviderFilterWith("Method testMain[1: ](Clazz)");
        Description description = setupDescription(true, "testMain[1: ](ClazzOther)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionHasNoMethodIdx() {

        // Given:
        setupDataProviderFilterWith("Method testMain[1: ](Clazz)");
        Description description = setupDescription(true, "testMain(Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionDoesNotHaveExpectedMethodIdx() {

        // Given:
        setupDataProviderFilterWith("Method testMain[1: ](Clazz)");
        Description description = setupDescription(true, "testMain[2: ](Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionHaveOnlyMethodNameAndEqualsExactly() {

        // Given:
        setupDataProviderFilterWith("Method testMain(Clazz)");
        Description description = setupDescription(true, "testMain(Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionHaveAdditionalMethodIdxAndEqualsMethodNameAndClass() {

        // Given:
        setupDataProviderFilterWith("Method testMain(Clazz)");
        Description description = setupDescription(true, "testMain[1: ](Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionHaveAddtionalMethodIdxAndEqualsExcatly() {

        // Given:
        setupDataProviderFilterWith("Method testMain[1: ](Clazz)");
        Description description = setupDescription(true, "testMain[1: ](Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionHaveAdditionalMethodIdxAndMethodParamsAreDifferentButIdxIsEqual() {

        // Given:
        setupDataProviderFilterWith("Method testMain[1: ](Clazz)");
        Description description = setupDescription(true, "testMain[1: test](Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueForMatchingChildDescription() {

        // Given:
        setupDataProviderFilterWith("Method testMain[1: ](Clazz)");

        Description description = setupDescription(false, "", setupDescription(true, "testMain[1: ](Clazz)"));

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueForMultipleChildDescriptionWithLastMatching() {

        // Given:
        setupDataProviderFilterWith("Method testMain[1: ](Clazz)");

        // @formatter:off
        Description description = setupDescription(false, "",
                setupDescription(true, "testOther[1: ](ClazzOther)"),
                setupDescription(false, "testOther[1: ](ClazzOther)"),
                setupDescription(true, "testMain[1: ](Clazz)")
            );
        // @formatter:on

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnFalseForMultipleChildAndFurtherChildDescriptionWithNonMatching() {

        // Given:
        setupDataProviderFilterWith("Method testMain[1: ](Clazz)");

        // @formatter:off
        Description description = setupDescription(false, "testMain[2: ](Clazz)",
                setupDescription( true, "testOther[1: ](ClazzOther)"),
                setupDescription( true,  "testMain[1: ](ClazzOther)"),
                setupDescription(false,  "testMain[1: ](Clazz)",
                        setupDescription( true,  "testMain[2: ](Clazz)"),
                        setupDescription( true, "testOther[1: ](ClazzOther)")
                    ),
                setupDescription( true, "testOther[1: ](Clazz)")
            );
        // @formatter:on

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testDescribeShouldReturnFilterDescripe() {

        // Given:
        setupDataProviderFilterWith("Method testMain[1: ](Clazz)");

        // When:
        String result = underTest.describe();

        // Then:
        assertThat(result).isEqualTo("Method testMain[1: ](Clazz)");
    }

    @Test
    public void testDescribtionPatternShouldNotMatchEmptyString() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testDescribtionPatternShouldMatchDescriptionWithoutParams() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("testMain(Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "testMain", null, null, "Clazz");
    }

    @Test
    public void testDescribtionPatternShouldMatchDescriptionWithParams() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("testMain[1: test](Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "testMain", "[1: test]", "1", "Clazz");
    }

    @Test
    public void testDescribtionPatternShouldMatchescriptionWithParamsContainingParentheses() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("testMain[1: (test)](Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "testMain", "[1: (test)]", "1", "Clazz");
    }

    @Test
    public void testDescribtionPatternShouldNotMatchDescriptionWithoutParamsAndSpaceInMethodName() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("Method testMain(Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testDescribtionPatternShouldNotMatchDescriptionWithParamsAndSpaceInMethodName() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("Method testMain[1: test](Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testDescribtionPatternShouldNotMatchDescriptionWithMethodNameContainingBrackets() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("Method test[M]ain(Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testDescribtionPatternShouldFindDescriptionWithParams() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("testMain[1: test](Clazz)");

        // When:
        boolean result = matcher.find();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "testMain", "[1: test]", "1", "Clazz");
    }

    @Test
    public void testDescribtionPatternShouldFindDescriptionWithoutParams() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("testMain[1: test](Clazz)");

        // When:
        boolean result = matcher.find();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "testMain", "[1: test]", "1", "Clazz");
    }

    @Test
    public void testDescribtionPatternShouldFindDescriptionWithMethodNameContainingBracketsAndNotHaveThemInGroup1() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("Method test[M]ain(Clazz)");

        // When:
        boolean result = matcher.find();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "ain", null, null, "Clazz");
    }

    private void setupDataProviderFilterWith(String filterDescriptionString) {
        filter = mock(Filter.class);
        doReturn(filterDescriptionString).when(filter).describe();
        underTest = new DataProviderFilter(filter);
    }

    private Description setupDescription(boolean isTest, String descriptionDisplayName,
            Description... childDescriptions) {

        Description description = mock(Description.class);
        doReturn(isTest).when(description).isTest();
        doReturn(descriptionDisplayName).when(description).getDisplayName();
        doReturn(new ArrayList<Description>(Arrays.asList(childDescriptions))).when(description).getChildren();
        return description;
    }

    private void assertThatMatcherGroupsAre(Matcher matcher, String... expectedGroups) {
        assertThat(matcher.groupCount()).as("group-count").isEqualTo(expectedGroups.length);
        for (int idx = 0; idx < expectedGroups.length; idx++) {
            assertThat(matcher.group(idx + 1)).as("group " + idx).isEqualTo(expectedGroups[idx]);
        }
    }
}