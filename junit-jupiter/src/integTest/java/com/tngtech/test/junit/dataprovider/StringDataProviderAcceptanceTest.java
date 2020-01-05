package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.math.RoundingMode;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.DataProviderExtension;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(DataProviderExtension.class)
@ExtendWith(UseDataProviderExtension.class)
class StringDataProviderAcceptanceTest {

    @DataProvider(splitBy = "\\|")
    static String[] dataProviderFileExistence() {
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

    @TestTemplate
    @UseDataProvider("dataProviderFileExistence")
    void testFileExistence(File file, boolean expected) {
        // Expect:
        assertThat(file.exists()).isEqualTo(expected);
    }

    // @formatter:off
    @TestTemplate
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
    @TestTemplate
    @DataProvider(value = {
        "               |  0",
        "a              |  1",
        "abc            |  3",
        "veryLongString | 14",
    }, splitBy = "\\|")
    // @formatter:off
    void testStringLength2(String str, int expectedLength) {
        // Expect:
        assertThat(str).hasSize(expectedLength);
    }

    // @formatter:off
    @TestTemplate
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
    static String[] dataProviderOldModeToRoundingModeUsingRegularDataprovider() {
        // @formatter:off
        return new String[] {
            "0, UP",
            "1, DOWN",
            "3, FLOOR",
        };
        // @formatter:on
    }

    @TestTemplate
    @UseDataProvider("dataProviderOldModeToRoundingModeUsingRegularDataprovider")
    void testOldModeToRoundingModeUsingRegularDataprovider(int oldMode, RoundingMode expected) {
        // Expect:
        assertThat(RoundingMode.valueOf(oldMode)).isEqualTo(expected);
    }

    @DataProvider({ "null", "", })
    void testIsEmptyString2(String str) {
        // When:
        boolean isEmpty = (str == null) || str.isEmpty();

        // Then:
        assertThat(isEmpty).isTrue();
    }
}
