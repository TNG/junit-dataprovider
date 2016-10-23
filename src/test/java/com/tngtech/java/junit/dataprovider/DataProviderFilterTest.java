package com.tngtech.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Filter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@RunWith(MockitoJUnitRunner.class)
public class DataProviderFilterTest extends BaseTest {

    @InjectMocks
    private DataProviderFilter underTest;

    @Mock
    private Filter filter;

    @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = NullPointerException.class)
    public void testDataProviderFilterShouldThrowNullPointerExceptionWhenFilterIsNull() {
        // Given:

        // When:
        @SuppressWarnings("unused")
        DataProviderFilter result = new DataProviderFilter(null);

        // Then: expect exception
    }

    @Test
    public void testShouldRunShouldCallOriginalFilterShouldRunIfOriginalFilterDescriptionCannotBeParsed() {
        // Given:
        doReturn("invalid").when(filter).describe();
        Description description = setupDescription(true, "test(com.tngtech.Clazz)");

        // When:
        underTest.shouldRun(description);

        // Then:
        verify(filter).describe();
        verify(filter).shouldRun(description);
        verifyNoMoreInteractions(filter);
    }

    @Test
    public void testShouldRunShouldCallOriginalFilterShouldRunIfIsTestAndGivenDescriptionCannotBeParsed() {
        // Given:
        doReturn("Method test(com.tngtech.Clazz)").when(filter).describe();
        Description description = setupDescription(true, "invalid");

        // When:
        underTest.shouldRun(description);

        // Then:
        verify(filter).describe();
        verify(filter).shouldRun(description);
        verifyNoMoreInteractions(filter);
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionDoesNotHaveExpectedMethodName() {
        // Given:
        doReturn("Method testMain[1: ](com.tngtech.Clazz)").when(filter).describe();
        Description description = setupDescription(true, "testOther[1: ](com.tngtech.Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionDoesNotHaveExpectedPackageName() {
        // Given:
        doReturn("Method testMain[1: ](com.tngtech.Clazz)").when(filter).describe();
        Description description = setupDescription(true, "testMain[1: ](com.tngtech.other.Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionDoesNotHaveExpectedClassName() {
        // Given:
        doReturn("Method testMain[1: ](com.tngtech.Clazz)").when(filter).describe();
        Description description = setupDescription(true, "testMain[1: ](com.tngtech.ClazzOther)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionHasNoMethodIdx() {
        // Given:
        doReturn("Method testMain[1: ](com.tngtech.Clazz)").when(filter).describe();
        Description description = setupDescription(true, "testMain(com.tngtech.Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionDoesNotHaveExpectedMethodIdx() {
        // Given:
        doReturn("Method testMain[1: ](com.tngtech.Clazz)").when(filter).describe();
        Description description = setupDescription(true, "testMain[2: ](com.tngtech.Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionHaveOnlyMethodNameAndEqualsExactlyWithoutPackage() {
        // Given:
        doReturn("Method testMain(Clazz)").when(filter).describe();
        Description description = setupDescription(true, "testMain(Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }


    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionHaveOnlyMethodNameAndEqualsExactly() {
        // Given:
        doReturn("Method testMain(com.tngtech.Clazz)").when(filter).describe();
        Description description = setupDescription(true, "testMain(com.tngtech.Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionHaveAdditionalMethodIdxAndEqualsMethodNameAndClass() {
        // Given:
        doReturn("Method testMain(com.tngtech.Clazz)").when(filter).describe();
        Description description = setupDescription(true, "testMain[1: ](com.tngtech.Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionHavingSomeRandomCodeBetweenMethodNameAndClassButFilterHasIndex() {
        // Given:
        doReturn("Method testMain[2: ](com.tngtech.Clazz)").when(filter).describe();

        Description description = setupDescription(true, "testMain 1, 2, 3(com.tngtech.Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
        verify(filter).shouldRun(description);
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionHavingSomeRandomCodeBetweenMethodNameAndClassButMethodNameIsNotTheSame() {
        // Given:
        doReturn("Method testMain(com.tngtech.Clazz)").when(filter).describe();

        Description description = setupDescription(true, "testOther: test, 4(com.tngtech.Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionHavingSomeRandomCodeBetweenMethodNameAndClassButClassIsNotTheSame() {
        // Given:
        doReturn("Method testMain(com.tngtech.Clazz)").when(filter).describe();

        Description description = setupDescription(true, "testMain 8zBZ=(qzt)487(com.tngtech.OtherClazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionHavingSomeRandomCodeBetweenMethodNameAndClass() {
        // Given:
        doReturn("Method testMain(com.tngtech.Clazz)").when(filter).describe();

        Description description = setupDescription(true, "testMain 298zBZ=)& %(/$(=93A SD4)i(qzt)487 5z2 59isf&(com.tngtech.Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionHaveAddtionalMethodIdxAndEqualsExcatly() {
        // Given:
        doReturn("Method testMain[1: ](com.tngtech.Clazz)").when(filter).describe();
        Description description = setupDescription(true, "testMain[1: ](com.tngtech.Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionHaveAdditionalMethodIdxAndMethodParamsAreDifferentButIdxIsEqual() {
        // Given:
        doReturn("Method testMain[1: ](com.tngtech.Clazz)").when(filter).describe();
        Description description = setupDescription(true, "testMain[1: test](com.tngtech.Clazz)");

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueForMatchingChildDescription() {
        // Given:
        doReturn("Method testMain[1: ](com.tngtech.Clazz)").when(filter).describe();

        Description description = setupDescription(false, "", setupDescription(true, "testMain[1: ](com.tngtech.Clazz)"));

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueForMultipleChildDescriptionWithLastMatching() {
        // Given:
        doReturn("Method testMain[1: ](com.tngtech.Clazz)").when(filter).describe();

        // @formatter:off
        Description description = setupDescription(false, "",
                setupDescription(true, "testOther[1: ](com.tngtech.ClazzOther)"),
                setupDescription(false, "testOther[1: ](com.tngtech.ClazzOther)"),
                setupDescription(true, "testMain[1: ](com.tngtech.Clazz)")
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
        doReturn("Method testMain[1: ](com.tngtech.Clazz)").when(filter).describe();

        // @formatter:off
        Description description = setupDescription(false, "testMain[2: ](com.tngtech.Clazz)",
                setupDescription( true, "testOther[1: ](com.tngtech.ClazzOther)"),
                setupDescription( true,  "testMain[1: ](com.tngtech.ClazzOther)"),
                setupDescription(false,  "testMain[1: ](com.tngtech.Clazz)",
                        setupDescription( true,  "testMain[2: ](com.tngtech.Clazz)"),
                        setupDescription( true, "testOther[1: ](com.tngtech.ClazzOther)")
                    ),
                setupDescription( true, "testOther[1: ](com.tngtech.Clazz)")
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
        doReturn("Method testMain[1: ](com.tngtech.Clazz)").when(filter).describe();

        // When:
        String result = underTest.describe();

        // Then:
        assertThat(result).isEqualTo("Method testMain[1: ](com.tngtech.Clazz)");
    }

    @Test
    public void testDescriptionPatternShouldNotMatchEmptyString() {
        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testDescriptionPatternShouldMatchDescriptionWithoutParams() {
        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("testMain(com.tngtech.Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "testMain", null, null, "com.tngtech.Clazz");
    }

    @Test
    public void testDescriptionPatternShouldMatchDescriptionWithParams() {
        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("testMain[1: test](com.tngtech.Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "testMain", "[1: test]", "1", "com.tngtech.Clazz");
    }

    @Test
    public void testDescriptionPatternShouldMatchDescriptionWithParamsContainingParentheses() {
        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("testMain[1: (test)](com.tngtech.Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "testMain", "[1: (test)]", "1", "com.tngtech.Clazz");
    }

    @Test
    public void testDescriptionPatternShouldMatchDescriptionWithParamsContainingNewline() {
        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("testMain[1: \n](com.tngtech.Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "testMain", "[1: \n]", "1", "com.tngtech.Clazz");
    }

    @Test
    public void testDescriptionPatternShouldNotMatchDescriptionWithoutParamsAndSpaceInMethodName() {
        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("Method test Main(com.tngtech.Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testDescriptionPatternShouldNotMatchDescriptionWithParamsAndSpaceInMethodName() {
        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("Method test Main[1: test](com.tngtech.Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testDescriptionPatternShouldNotMatchDescriptionWithMethodNameContainingBracketsAndNotHaveThemInGroup1() {
        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("Method test[M]ain(com.tngtech.Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testDescriptionPatternShouldFindDescriptionWithMethodNameContainingBracketsAndNotHaveThemInGroup1() {
        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("Method test[M]ain(com.tngtech.Clazz)");

        // When:
        boolean result = matcher.find();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "ain", null, null, "com.tngtech.Clazz");
    }

    @Test
    public void testGenerousDescriptionPatternShouldMatchDescriptionContainingCorrectMethodNameAndClazz() {
        // Given:
        Matcher matcher = DataProviderFilter.GENEROUS_DESCRIPTION_PATTERN.matcher("testMain whatever => ignore(Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "testMain", " whatever => ignore", " whatever => ignore", "Clazz", null);
    }

    @Test
    public void testGenerousDescriptionPatternShouldMatchDescriptionContainingCorrectMethodNameAndPackageAndClazz() {
        // Given:
        Matcher matcher = DataProviderFilter.GENEROUS_DESCRIPTION_PATTERN.matcher("testMain whatever => ignore(com.tngtech.Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "testMain", " whatever => ignore", " whatever => ignore", "com.tngtech.Clazz", "tngtech.");
    }

    @Test
    public void testGenerousDescriptionPatternShouldNotMatchDescriptionContainingIncorrectPackageName() {
        // Given:
        Matcher matcher = DataProviderFilter.GENEROUS_DESCRIPTION_PATTERN.matcher("testMain(com.tng?tech.Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testGenerousDescriptionPatternShouldNotMatchDescriptionContainingIncorrectClazzName() {
        // Given:
        Matcher matcher = DataProviderFilter.GENEROUS_DESCRIPTION_PATTERN.matcher("testMain(Cla?zz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testGenerousDescriptionPatternShouldMatchDescriptionWithParamsContainingNewline() {
        // Given:
        Matcher matcher = DataProviderFilter.GENEROUS_DESCRIPTION_PATTERN.matcher("testMain[1: \r](Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isTrue();
        assertThatMatcherGroupsAre(matcher, "testMain", "[1: \r]", "[1: \r]", "Clazz", null);
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
