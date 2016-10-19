package com.tngtech.test.java.junit.dataprovider;

import static com.tngtech.java.junit.dataprovider.DataProviders.$;
import static com.tngtech.java.junit.dataprovider.DataProviders.$$;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;

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
    public static Object[][] testIsEmptyString() {
        // @formatter:off
        return new Object[][] {
                { null },
                { "" },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider
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
        return $$(
                $( -1, -1, -2 ),
                $( -1,  0, -1 ),
                $(  0, -1, -1 ),
                $(  0,  0,  0 ),
                $(  0,  1,  1 ),
                $(  1,  0,  1 ),
                $(  1,  1,  2 )
        );
        // @formatter:on
    }

    @Test
    @UseDataProvider
    public void testAdd(int a, int b, int expected) {
        // Given:

        // When:
        int result = a + b;

        // Then:
        assertThat(result).isEqualTo(expected);
    }

    @DataProvider(format = "%m: %p[0] * %p[1] == %p[2]")
    public static Object[][] dataProviderMultiply() {
        // @formatter:off
        return new Object[][] {
                {  0,  0,  0 },
                { -1,  0,  0 },
                {  0,  1,  0 },
                {  1,  1,  1 },
                {  1, -1, -1 },
                { -1, -1,  1 },
                {  1,  2,  2 },
                { -1,  2, -2 },
                { -1, -2,  2 },
                { -1, -2,  2 },
                {  6,  7, 42 },
            };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderMultiply")
    public void testMultiply(int a, int b, int expected) {
        // Expect:
        assertThat(a * b).isEqualTo(expected);
    }

    @DataProvider
    public static Object[][] dataProviderMinus() {
        // @formatter:off
        return $$(
                $(  0,  0,  0 ),
                $(  0,  1, -1 ),
                $(  0, -1,  1 ),
                $(  1,  0,  1 ),
                $(  1,  1,  0 ),
                $( -1,  0, -1 ),
                $( -1, -1,  0 )
        );
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderMinus")
    public void testMinus(long a, long b, long expected) {
        // Given:

        // When:
        long result = a - b;

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

    @DataProvider(splitBy = "\\|", trimValues = true)
    public static String[] dataProviderFileExistence() {
        // @formatter:off
        return new String[] {
                "src             | true",
                "src/main        | true",
                "src/main/java/  | true",
                "src/test/java/  | true",
                "test            | false",
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderFileExistence")
    public void testFileExistence(File file, boolean expected) {
        // Expect:
        assertThat(file.exists()).isEqualTo(expected);
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

    @DataProvider
    public static List<? extends Number> dataProviderIsNumber() {
        List<Number> result = new ArrayList<Number>();
        result.add(101);
        result.add(125L);
        result.add(125.0);
        return result;
    }

    @Test
    @UseDataProvider("dataProviderIsNumber")
    public void testIsNumber(Number number) {
        // Expect:
        assertThat(number).isInstanceOf(Number.class);
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
    @DataProvider(value = {
        "               |  0",
        "a              |  1",
        "abc            |  3",
        "veryLongString | 14",
    }, splitBy = "\\|", trimValues = true, convertNulls = true)
    // @formatter:off
    public void testStringLength2(String str, int expectedLength) {
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
    // @formatter:on
    public void testOldModeToRoundingMode(int oldMode, RoundingMode expected) {
        // Expect:
        assertThat(RoundingMode.valueOf(oldMode)).isEqualTo(expected);
    }

    @DataProvider
    public static String[] dataProviderOldModeToRoundingModeUsingRegularDataprovidert() {
        // @formatter:off
        return new String[] {
            "0, UP",
            "1, DOWN",
            "3, FLOOR",
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderOldModeToRoundingModeUsingRegularDataprovidert")
    public void testOldModeToRoundingModeUsingRegularDataprovider(int oldMode, RoundingMode expected) {
        // Expect:
        assertThat(RoundingMode.valueOf(oldMode)).isEqualTo(expected);
    }

    @Test
    @DataProvider({ "null", "", })
    public void testIsEmptyString2(String str) {
        // When:
        boolean isEmpty = (str == null) ? true : str.isEmpty();

        // Then:
        assertThat(isEmpty).isTrue();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExternalFile {
        public enum Format {
            CSV,
            XML,
            XLS;
        }

        Format format();

        String value();
    }

    @DataProvider
    public static Object[][] loadFromExternalFile(FrameworkMethod testMethod) {
        String testDataFile = testMethod.getAnnotation(ExternalFile.class).value();
        // Load the data from the external file here ...
        return new Object[][] { { testDataFile } };
    }

    @Test
    @UseDataProvider("loadFromExternalFile")
    @ExternalFile(format = ExternalFile.Format.CSV, value = "testdata.csv")
    public void testThatUsesUniversalDataProvider(String testData) {
        // Expect:
        assertThat(testData).isEqualTo("testdata.csv");
    }

    @DataProvider
    public static Object[][] dataProviderWithStringContainingTabsNewlineAndCarriageReturn() {
        Object[][] result = { {  } };
        return result;
    }

    @Test
    @DataProvider({ "Do it.\nOr let it." })
    public void testWithStringContainingTabsNewlineAndCarriageReturn(@SuppressWarnings("unused") String string) {
        // nothing to do => Just look at the test output in Eclispe's JUnit view if it is displayed correctly
    }
}
