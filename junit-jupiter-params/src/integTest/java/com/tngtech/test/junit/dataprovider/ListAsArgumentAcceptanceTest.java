package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class ListAsArgumentAcceptanceTest {

    @DataProvider
    static Object[][] dataProviderListArg() {
        // @formatter:off
        return new Object[][] {
            { Arrays.asList("a", "b"), "c" },
        };
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider
    void testListArg(List<String> list, String string) {
        // Expected:
        assertThat(list).doesNotContain(string);
    }

    @DataProvider
    static Object[][] stringsData() {
        return new Object[][] {
                { Arrays.asList("string1", "stringValue"), "stringValue" }
        };
    }

    @ParameterizedTest
    @UseDataProvider("stringsData")
    void test(List<String> strings, String expectedValue) {
        // Expected:
        assertThat(strings).contains(expectedValue);
    }
}