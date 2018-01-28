package com.tngtech.test.junit.dataprovider.custom.meta;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class MetaAnnotationAcceptanceTest {

    // @formatter:off
    @StringDataProvider({
        "               |  0",
        "a              |  1",
        "abc            |  3",
        "veryLongString | 14",
    })
    // @formatter:off
    void testStringLength(String str, int expectedLength) {
        // Expect:
        assertThat(str.length()).isEqualTo(expectedLength);
    }

    @FixedLocationDataProviderTest
    void testAdd(int a, int b, int expected) throws Exception {
        // Expect:
        assertThat(a + b).isEqualTo(expected);
    }

    @Nested
    class NestedTests {
        @FixedLocationDataProviderTest
        void testAdd(int a, int b, int expected) {
            // Expect:
            assertThat(a + b).isEqualTo(expected);
        }
    }

    @DataProviderTest(value = "dataProviderMinus", location = DataProviderLocation.class)
    void testMinus(long a, long b, long expected) {
        // Given:

        // When:
        long result = a - b;

        // Then:
        assertThat(result).isEqualTo(expected);
    }

    // @formatter:off
    @ParameterizedTest
    @DataProvider({
            "null",
        })
    // @formatter:on
    void testIsNull(String string) {
        // Expect:
        assertThat(string).isNull();
    }

    @DataProvider
    static String[] dataProviderNonNull() {
        // @formatter:off
        return new String[] {
            "NULL",
            "Null",
        };
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider
    void testNonNull(String nonNull) {
        // Expect:
        assertThat(nonNull).isNotNull();
    }
}
