package com.tngtech.java.junit.dataprovider;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class DataProviderFilterTest {

    @InjectMocks
    private DataProviderFilter underTest;

    @Mock
    private Filter filter;

    @Before
    public void setup() {
        filter = mock(Filter.class);
        doReturn("Method testMain[1: ](Clazz)").when(filter).describe();
        underTest = new DataProviderFilter(filter);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = IllegalArgumentException.class)
    public void testDataProviderFilterShouldThrowNullPointerExceptionWhenFilterIsNull() {

        // Given:

        // When:
        @SuppressWarnings("unused")
        DataProviderFilter dataProviderFilter = new DataProviderFilter(null);

        // Then: expect exception
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DLS_DEAD_LOCAL_STORE")
    @Test(expected = IllegalArgumentException.class)
    public void testDataProviderFilterShouldThrowIllegalArgumentExceptionWhenFilterDescriptionCannotBeParsed() {

        // Given:
        doReturn("invalid").when(filter).describe();

        // When:
        @SuppressWarnings("unused")
        DataProviderFilter dataProviderFilter = new DataProviderFilter(filter);

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldRunShouldThrowIllegalArgumentExceptionWhenDescriptionCannotBeParsed() {

        // Given:
        Description description = mock(Description.class);
        doReturn(true).when(description).isTest();
        doReturn("invalid").when(description).getDisplayName();

        // When:
        underTest.shouldRun(description);

        // Then: expect exception
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionDoesNotHaveExpectedMethodName() {

        // Given:
        Description description = mock(Description.class);
        doReturn(true).when(description).isTest();
        doReturn("testOther[1: ](Clazz)").when(description).getDisplayName();

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionDoesNotHaveExpectedClassName() {

        // Given:
        Description description = mock(Description.class);
        doReturn(true).when(description).isTest();
        doReturn("testMain[1: ](ClazzOther)").when(description).getDisplayName();

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionHasNoMethodIdx() {

        // Given:
        Description description = mock(Description.class);
        doReturn(true).when(description).isTest();
        doReturn("testMain(Clazz)").when(description).getDisplayName();

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnFalseWhenDescriptionDoesNotHaveExpectedMethodIdx() {

        // Given:
        Description description = mock(Description.class);
        doReturn(true).when(description).isTest();
        doReturn("testMain[2: ](Clazz)").when(description).getDisplayName();

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionEqualsExpectedWithAdditionalMethodParams() {

        // Given:
        doReturn("Method testMain(Clazz)").when(filter).describe();
        underTest = new DataProviderFilter(filter);

        Description description = mock(Description.class);
        doReturn(true).when(description).isTest();
        doReturn("testMain[1: test](Clazz)").when(description).getDisplayName();

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionEqualsExpected() {

        // Given:
        Description description = mock(Description.class);
        doReturn(true).when(description).isTest();
        doReturn("testMain[1: ](Clazz)").when(description).getDisplayName();

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueWhenDescriptionEqualsExpectedButMethodParamsAreDifferent() {

        // Given:
        Description description = mock(Description.class);
        doReturn(true).when(description).isTest();
        doReturn("testMain[1: test](Clazz)").when(description).getDisplayName();

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueForMatchingChildDescription() {

        // Given:
        Description childDescription = mock(Description.class);
        doReturn(true).when(childDescription).isTest();
        doReturn("testMain[1: ](Clazz)").when(childDescription).getDisplayName();

        Description description = mock(Description.class);
        List<Description> children = new ArrayList<Description>();
        children.add(childDescription);
        doReturn(children).when(description).getChildren();

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testShouldRunShouldReturnTrueForMultipleChildDescriptionWithLastMatching() {

        // Given:
        Description childDescription1 = mock(Description.class);
        doReturn(true).when(childDescription1).isTest();
        doReturn("testOther[1: ](ClazzOther)").when(childDescription1).getDisplayName();

        Description childDescription2 = mock(Description.class);
        doReturn(true).when(childDescription2).isTest();
        doReturn("testMain[1: ](Clazz)").when(childDescription2).getDisplayName();

        Description description = mock(Description.class);
        List<Description> children = new ArrayList<Description>();
        children.add(childDescription1);
        children.add(childDescription2);
        doReturn(children).when(description).getChildren();

        // When:
        boolean result = underTest.shouldRun(description);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testDescribe() {

        // Given:

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
        assertThat(matcher.group(DataProviderFilter.GROUP_METHOD_NAME)).isEqualTo("testMain");
        assertThat(matcher.group(DataProviderFilter.GROUP_METHOD_PARAMS)).isNull();
        assertThat(matcher.group(DataProviderFilter.GROUP_METHOD_IDX)).isNull();
        assertThat(matcher.group(DataProviderFilter.GROUP_CLASS)).isEqualTo("Clazz");
    }

    @Test
    public void testDescribtionPatternShouldMatchDescriptionWithParams() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("testMain[1: test](Clazz)");

        // When:
        boolean result = matcher.matches();

        // Then:
        assertThat(result).isTrue();
        assertThat(matcher.group(DataProviderFilter.GROUP_METHOD_NAME)).isEqualTo("testMain");
        assertThat(matcher.group(DataProviderFilter.GROUP_METHOD_PARAMS)).isEqualTo("[1: test]");
        assertThat(matcher.group(DataProviderFilter.GROUP_METHOD_IDX)).isEqualTo("1");
        assertThat(matcher.group(DataProviderFilter.GROUP_CLASS)).isEqualTo("Clazz");
    }

    @Test
    public void testDescribtionPatternShouldFindDescriptionWithoutParams() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("Method testMain(Clazz)");

        // When:
        boolean result = matcher.find();

        // Then:
        assertThat(result).isTrue();
        assertThat(matcher.group(DataProviderFilter.GROUP_METHOD_NAME)).isEqualTo("testMain");
        assertThat(matcher.group(DataProviderFilter.GROUP_METHOD_PARAMS)).isNull();
        assertThat(matcher.group(DataProviderFilter.GROUP_METHOD_IDX)).isNull();
        assertThat(matcher.group(DataProviderFilter.GROUP_CLASS)).isEqualTo("Clazz");
    }

    @Test
    public void testDescribtionPatternShouldFindDescriptionWithParams() {

        // Given:
        Matcher matcher = DataProviderFilter.DESCRIPTION_PATTERN.matcher("Method testMain[1: test](Clazz)");

        // When:
        boolean result = matcher.find();

        // Then:
        assertThat(result).isTrue();
        assertThat(matcher.group(DataProviderFilter.GROUP_METHOD_NAME)).isEqualTo("testMain");
        assertThat(matcher.group(DataProviderFilter.GROUP_METHOD_PARAMS)).isEqualTo("[1: test]");
        assertThat(matcher.group(DataProviderFilter.GROUP_METHOD_IDX)).isEqualTo("1");
        assertThat(matcher.group(DataProviderFilter.GROUP_CLASS)).isEqualTo("Clazz");
    }

}
