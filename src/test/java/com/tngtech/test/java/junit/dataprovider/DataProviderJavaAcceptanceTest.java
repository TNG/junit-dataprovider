package com.tngtech.test.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderJavaAcceptanceTest {

    @Test
    public void testAddWithoutDataProvider() {
        // Given:

        // When:
        int result = 1 + 2;

        // Then:
        assertThat(result).isEqualTo(3);
    }

    @DataProvider
    public static Object[][] dataProviderStringIsNullOrEmpty() {
        // @formatter:off
        return new Object[][] {
                { null },
                { "" },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderStringIsNullOrEmpty")
    public void testIsEmptyString(String str) {
        // Given:

        // When:
        boolean isEmpty = (str == null) ? true : str.isEmpty();

        // Then:
        assertThat(isEmpty).isTrue();
    }

    @Test
    @UseDataProvider(value = "dataProviderIsStringLengthGreaterTwo", location = StringDataProvider.class)
    public void testIsStringLengthGreaterThanTwo(String str, boolean expected) {

        // Given:

        // When:
        boolean isGreaterThanTwo = (str == null) ? false : str.length() > 2;

        // Then:
        assertThat(isGreaterThanTwo).isEqualTo(expected);
    }

    @DataProvider
    public static Object[][] dataProviderAdd() {
        // @formatter:off
        return new Object[][] {
                { -1, -1, -2 },
                { -1,  0, -1 },
                {  0, -1, -1 },
                {  0,  0,  0 },
                {  0,  1,  1 },
                {  1,  0,  1 },
                {  1,  1,  2 },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderAdd")
    public void testAdd(int a, int b, int expected) {
        // Given:

        // When:
        int result = a + b;

        // Then:
        assertThat(result).isEqualTo(expected);
    }

    @DataProvider
    public static Object[][] dataProviderWithNonConstantObjects() {

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        Calendar now = Calendar.getInstance();

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        // @formatter:off
        return new Object[][] {
                { yesterday,    yesterday,      false },
                { yesterday,    now,            true },
                { yesterday,    tomorrow,       true },

                { now,          yesterday,      false },
                { now,          now,            false },
                { now,          tomorrow,       true },

                { tomorrow,     yesterday,      false },
                { tomorrow,     now,            false },
                { tomorrow,     tomorrow,       false },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderWithNonConstantObjects")
    public void testWithNonConstantObjects(Calendar cal1, Calendar cal2, boolean cal1IsEarlierThenCal2) {
        // Given:

        // When:
        boolean result = cal1.before(cal2);

        // Then:
        assertThat(result).isEqualTo(cal1IsEarlierThenCal2);
    }

    @DataProvider
    public static List<List<Object>> dataProviderNumberFormat() {

        List<List<Object>> result = new ArrayList<List<Object>>();
        List<Object> first = new ArrayList<Object>();
        first.add(Integer.valueOf(101));
        first.add("%5d");
        first.add("  101");
        result.add(first);
        List<Object> second = new ArrayList<Object>();
        second.add(125);
        second.add("%06d");
        second.add("000125");
        result.add(second);
        return result;
    }

    @Test
    @UseDataProvider("dataProviderNumberFormat")
    public void testNumberFormat(Number number, String format, String expected) {
        // Given:

        // When:
        String result = String.format(format, number);

        // Then:
        assertThat(result).isEqualTo(expected);
    }

    // @formatter:off
    @Test
    @DataProvider({
            ",                 0",
            "a,                1",
            "abc,              3",
            "veryLongString,  14",
        })
    // @formatter:off
    public void testStringLength(String str, int expectedLength) {
        // Expect:
        assertThat(str.length()).isEqualTo(expectedLength);
    }

    // @formatter:off
    @Test
    @DataProvider({
        "0, UP",
        "1, DOWN",
        "3, FLOOR",
    })
    // @formatter:off
    public void testOldModeToRoundingMode(int oldMode, RoundingMode expected) {
        // When:
        RoundingMode result = RoundingMode.valueOf(oldMode);

        // Then:
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DataProvider({
            "null",
            "",
        })
    public void testIsEmptyString2(String str) {
        // When:
        boolean isEmpty = (str == null) ? true : str.isEmpty();

        // Then:
        assertThat(isEmpty).isTrue();
    }
}
