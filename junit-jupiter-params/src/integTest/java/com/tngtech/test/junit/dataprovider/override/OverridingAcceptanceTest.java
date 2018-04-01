package com.tngtech.test.junit.dataprovider.override;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class OverridingAcceptanceTest extends AbstractAcceptanceBaseTest {

    @Override
    @ParameterizedTest
    @UseDataProvider
    void testBase(String one) {
        assertThat(one).isEqualTo("1");
    }

    @ParameterizedTest
    @UseDataProvider("dataProviderBase")
    public void testBaseNotOverridden(String one) {
        assertThat(one).isEqualTo("1");
    }

    @DataProvider
    public static Object[][] dataProviderChild() {
        return new Object[][] { { "1" } };
    }

}
