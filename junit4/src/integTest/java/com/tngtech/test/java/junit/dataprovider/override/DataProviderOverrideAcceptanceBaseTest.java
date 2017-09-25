package com.tngtech.test.java.junit.dataprovider.override;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderOverrideAcceptanceBaseTest {
    @DataProvider
    public static Object[][] dataProvider() {
        return new Object[][] { { "1" } };
    }

    @Test
    @UseDataProvider
    public void test(String one) {

    }
}