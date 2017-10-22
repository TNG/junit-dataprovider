package com.tngtech.test.junit.dataprovider.custom.placeholder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;

// Note: no pendent in junit-jupiter-params to use custom placeholder / formatter such that arguments are normally displayed
class CustomPlaceholderAcceptanceTest {

    // @formatter:off
    @ParameterizedTest
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
