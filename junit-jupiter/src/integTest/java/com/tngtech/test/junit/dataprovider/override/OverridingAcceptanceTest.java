package com.tngtech.test.junit.dataprovider.override;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(UseDataProviderExtension.class)
class OverridingAcceptanceTest extends ToBeOverriddenAcceptanceTest {

    @Override
    @TestTemplate
    @UseDataProvider
    void test(String one) {
        assertThat(one).isEqualTo("1");
    }
}
