package com.tngtech.test.junit.dataprovider.override;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class ToBeOverriddenAcceptanceTest {
    @DataProvider
    static Object[][] dataProvider() {
        return new Object[][] { { "1" } };
    }

    @ParameterizedTest
    @UseDataProvider
    void test(String one) {
        assertThat(one).isEqualTo("1");
    }
}