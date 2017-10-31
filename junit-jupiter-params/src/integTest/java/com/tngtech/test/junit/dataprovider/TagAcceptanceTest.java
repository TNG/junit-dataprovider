package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

@Tag("two")
class TagAcceptanceTest {

    @Test
    void testNone() {
        // Expect:
        assertThat("none").hasSize(4);
    }

    @Tag("one")
    @Test
    void testOne() {
        // Expect:
        assertThat("one").hasSize(3);
    }

    @DataProvider
    static Object[][] dataProvider() {
        // @formatter:off
        return new Object[][] {
                { "",    0 },
                { "1",   1 },
                { "12",  2 },
        };
        // @formatter:on
    }

    @Tag("one")
    @ParameterizedTest
    @UseDataProvider("dataProvider")
    void test(String string, int expectedLength) {
        // Expect:
        assertThat(string).hasSize(expectedLength);
    }
}
