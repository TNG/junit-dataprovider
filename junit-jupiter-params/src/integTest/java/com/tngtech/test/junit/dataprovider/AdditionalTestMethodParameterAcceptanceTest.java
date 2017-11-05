package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class AdditionalTestMethodParameterAcceptanceTest {

    @DataProvider
    static Object[][] dataProviderObjectArrayArray() {
        // @formatter:off
        return new Object[][] {
                {   0,   1, "0, 1" },
                { 'a', 'b', "a, b" },
            };
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider
    void testObjectArrayArray(Object a, Object b, String expected, TestInfo testInfo) {
        // Expect:
        assertThat(testInfo.getDisplayName()).contains(expected);
    }

    @DataProvider
    static Object[] dataProviderObjectArray() {
        // @formatter:off
        return new Object[] {
                'a',
                "a",
            };
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider
    void testObjectArray(Object a, TestInfo testInfo) {
        // Expect:
        assertThat(testInfo.getDisplayName()).contains("a");
    }

    @ParameterizedTest
    // @formatter:off
    @DataProvider(value = {
            "2 | 3 | 2, 3",
            "c | d | c, d",
        }, splitBy = "\\|")
    // @formatter:on
    void testThree(String a, String b, String expected, TestInfo testInfo) {
        // Expect:
        assertThat(testInfo.getDisplayName()).contains(expected);
    }
}
