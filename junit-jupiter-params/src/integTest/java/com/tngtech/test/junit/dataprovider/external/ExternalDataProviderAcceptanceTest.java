package com.tngtech.test.junit.dataprovider.external;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.UseDataProvider;

class ExternalDataProviderAcceptanceTest {

    @ParameterizedTest
    @UseDataProvider(value = "dataProviderIsStringLengthGreaterTwo", location = ExternalDataProvider.class)
    void testIsStringLengthGreaterThanTwo(String str, boolean expected) {
        // Given:

        // When:
        boolean isGreaterThanTwo = (str != null) && str.length() > 2;

        // Then:
        assertThat(isGreaterThanTwo).isEqualTo(expected);
    }
}