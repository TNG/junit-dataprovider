package com.tngtech.test.junit.dataprovider.override;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.jupiter.api.TestTemplate;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

abstract class AbstractAcceptanceBaseTest {

    @DataProvider
    static Object[][] dataProviderBase() {
        return new Object[][] { { "1" } };
    }

    @TestTemplate
    @UseDataProvider
    void testBase(@SuppressWarnings("unused") String one) {
        fail("should be overridden and therefore not fail");
    }

    @TestTemplate
    @UseDataProvider
    public void testChild(String one) {
        assertThat(one).isEqualTo("1");
    }
}