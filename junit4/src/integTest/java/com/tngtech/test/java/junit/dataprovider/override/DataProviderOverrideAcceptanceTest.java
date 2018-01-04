package com.tngtech.test.java.junit.dataprovider.override;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderOverrideAcceptanceTest extends DataProviderOverrideAcceptanceBaseTest {

    @Override
    @Test
    @UseDataProvider
    public void testBase(String one) {
        assertThat(one).isEqualTo("1");
    }

    @Test
    @UseDataProvider("dataProviderBase")
    public void testBaseNotOverridden(String one) {
        assertThat(one).isEqualTo("1");
    }

    @DataProvider
    public static Object[][] dataProviderChild() {
        return new Object[][] { { "1" } };
    }
}
