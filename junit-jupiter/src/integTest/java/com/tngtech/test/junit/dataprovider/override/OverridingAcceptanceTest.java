package com.tngtech.test.junit.dataprovider.override;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(UseDataProviderExtension.class)
class OverridingAcceptanceTest extends AbstractAcceptanceBaseTest {

    @Override
    @TestTemplate
    @UseDataProvider
    void testBase(String one) {
        assertThat(one).isEqualTo("1");
    }

    @TestTemplate
    @UseDataProvider("dataProviderBase")
    public void testBaseNotOverridden(String one) {
        assertThat(one).isEqualTo("1");
    }

    @DataProvider
    public static Object[][] dataProviderChild() {
        return new Object[][] { { "1" } };
    }

}
