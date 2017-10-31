package com.tngtech.test.junit.dataprovider.custom.placeholder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;

@ExtendWith(CustomPlaceholderDataProviderExtension.class)
class CustomPlaceholderAcceptanceTest {

    // @formatter:off
    @TestTemplate
    @DataProvider({
        "veryVeryLongMethodNameWhichMustBeStripped,                                      null, false",
        "veryVeryLongMethodNameWhichMustBeStripped,                                          , false",
        "veryVeryLongMethodNameWhichMustBeStripped, veryVeryLongMethodNameWhichMustBeStripped,  true",
        "veryverylongmethodnamewhichmustbestripped, veryVeryLongMethodNameWhichMustBeStripped,  true",
        "veryVeryLongMethodNameWhichMustBeStripped, veryverylongmethodnamewhichmustbestripped,  true"
    })
    // @formatter:on
    void testEqualsIgnoreCase(String methodName1, String methodName2, boolean expected) {
        // Expected:
        assertThat(methodName1.equalsIgnoreCase(methodName2)).isEqualTo(expected);
    }
}
