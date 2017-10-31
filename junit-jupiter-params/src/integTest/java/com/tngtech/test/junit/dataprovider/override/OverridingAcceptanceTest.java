package com.tngtech.test.junit.dataprovider.override;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.UseDataProvider;

class OverridingAcceptanceTest extends ToBeOverriddenAcceptanceTest {

    @Override
    @ParameterizedTest
    @UseDataProvider
    void test(String one) {
        assertThat(one).isEqualTo("1");
    }
}
