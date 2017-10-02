package com.tngtech.test.junit.dataprovider.external;

import com.tngtech.junit.dataprovider.DataProvider;

class ExternalDataProvider {

    @DataProvider
    static Object[][] dataProviderIsStringLengthGreaterTwo() {
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
