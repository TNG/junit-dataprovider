package com.tngtech.test.junit.dataprovider.external;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProviderExtension;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(DataProviderExtension.class)
@ExtendWith(UseDataProviderExtension.class)
class ExternalDataProviderAcceptanceTest {

    @TestTemplate
    @UseDataProvider(value = "dataProviderIsStringLengthGreaterTwo", location = ExternalDataProvider.class)
    void testIsStringLengthGreaterThanTwo(String str, boolean expected) {
        // Given:

        // When:
        boolean isGreaterThanTwo = (str == null) ? false : str.length() > 2;

        // Then:
        assertThat(isGreaterThanTwo).isEqualTo(expected);
    }
}