package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class BeforeAcceptanceTest {

    private static Object staticField;
    private Object field;

    @BeforeAll
    static void setupClass() {
        staticField = Integer.valueOf(1);
    }

    @BeforeEach
    void setup() {
        field = Integer.valueOf(1);
    }

    @DataProvider
    static Object[][] dataProviderFieldIsEqualTo() {
        // @formatter:off
        return new Object[][] {
            { 1 },
            { staticField },
        };
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider
    void testFieldIsEqualTo(Object obj) {
        // Expect:
        assertThat(field).isEqualTo(obj);
    }

    @DataProvider
    static Object[][] dataProviderFieldIsNotEqualTo() {
        // @formatter:off
        return new Object[][] {
                { null },
                { "" },
                { Integer.valueOf(2) },
        };
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider
    void testFieldIsNotEqualTo(Object obj) {
        // Expect:
        assertThat(field).isNotEqualTo(obj);
    }
}
