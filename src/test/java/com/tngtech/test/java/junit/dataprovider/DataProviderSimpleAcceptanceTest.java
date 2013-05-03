package com.tngtech.test.java.junit.dataprovider;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderSimpleAcceptanceTest {

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
}
