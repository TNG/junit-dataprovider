package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.math.RoundingMode;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

@TestInstance(Lifecycle.PER_CLASS)
class StringDataProviderAcceptanceTest {

    @DataProvider(splitBy = "\\|", trimValues = true)
    String[] dataProviderFileExistence() {
        // @formatter:off
        return new String[] {
                "src             |  true",
                "src/main        |  true",
                "src/main/java/  |  true",
                "src/test/java/  |  true",
                "test            | false",
        };
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider("dataProviderFileExistence")
    void testFileExistence(File file, boolean expected) {
        // Expect:
        assertThat(file.exists()).isEqualTo(expected);
    }

    // @formatter:off
    @ParameterizedTest
    @DataProvider({
            ",                 0",
            "a,                1",
            "abc,              3",
            "veryLongString,  14",
        })
    // @formatter:off
    void testStringLength(String str, int expectedLength) {
        // Expect:
        assertThat(str).hasSize(expectedLength);
    }

    // @formatter:off
    @ParameterizedTest
    @DataProvider(value = {
        "               |  0",
        "a              |  1",
        "abc            |  3",
        "veryLongString | 14",
    }, splitBy = "\\|", trimValues = true, convertNulls = true)
    // @formatter:off
    void testStringLength2(String str, int expectedLength) {
        // Expect:
        assertThat(str).hasSize(expectedLength);
    }

    // @formatter:off
    @ParameterizedTest
    @DataProvider({
            "0, UP",
            "1, DOWN",
            "3, FLOOR",
        })
    // @formatter:on
    void testOldModeToRoundingMode(int oldMode, RoundingMode expected) {
        // Expect:
        assertThat(RoundingMode.valueOf(oldMode)).isEqualTo(expected);
    }

    @DataProvider
    String[] dataProviderOldModeToRoundingModeUsingRegularDataprovider() {
        // @formatter:off
        return new String[] {
            "0, UP",
            "1, DOWN",
            "3, FLOOR",
        };
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider("dataProviderOldModeToRoundingModeUsingRegularDataprovider")
    void testOldModeToRoundingModeUsingRegularDataprovider(int oldMode, RoundingMode expected) {
        // Expect:
        assertThat(RoundingMode.valueOf(oldMode)).isEqualTo(expected);
    }

    @DataProvider({ "null", "", })
    void testIsEmptyString2(String str) {
        // When:
        boolean isEmpty = (str == null) ? true : str.isEmpty();

        // Then:
        assertThat(isEmpty).isTrue();
    }
}
