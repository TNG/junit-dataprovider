package com.tngtech.test.java.junit.dataprovider.override;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderOverrideAcceptanceTest extends DataProviderOverrideAcceptanceBaseTest {

    @Override
    @Test
    @UseDataProvider
    public void test(String one) {

    }

}
