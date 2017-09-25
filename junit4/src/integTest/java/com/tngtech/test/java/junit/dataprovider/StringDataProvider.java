package com.tngtech.test.java.junit.dataprovider;

import com.tngtech.java.junit.dataprovider.DataProvider;

public class StringDataProvider {

    @DataProvider
    public static Object[][] dataProviderIsStringLengthGreaterTwo() {
        // @formatter:off
        return new Object[][] {
                { "",       false },
                { "1",      false },
                { "12",     false },
                { "123",    true },
                { "Test",   true },
            };
        // @formatter:on
    }
}
