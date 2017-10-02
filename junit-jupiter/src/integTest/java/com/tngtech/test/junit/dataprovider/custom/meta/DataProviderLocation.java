package com.tngtech.test.junit.dataprovider.custom.meta;

import static com.tngtech.junit.dataprovider.DataProviders.$;
import static com.tngtech.junit.dataprovider.DataProviders.$$;

import com.tngtech.junit.dataprovider.DataProvider;

class DataProviderLocation {

    @DataProvider
    static Object[][] dataProviderAdd() {
        //@formatter:off
        return new Object[][] {
            {  0,  0,  0 },
            {  0,  1,  1 },
            {  1,  0,  1 },
            {  1,  1,  2 },

            {  0, -1, -1 },
            { -1, -1, -2 },
        };
        //@formatter:on
    }

    @DataProvider
    static Object[][] dataProviderMinus() {
        // @formatter:off
        return $$(
                $(  0,  0,  0 ),
                $(  0,  1, -1 ),
                $(  0, -1,  1 ),
                $(  1,  0,  1 ),
                $(  1,  1,  0 ),
                $( -1,  0, -1 ),
                $( -1, -1,  0 )
        );
        // @formatter:on
    }
}
