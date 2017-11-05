package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.DataProviderExtension;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(DataProviderExtension.class)
@ExtendWith(UseDataProviderExtension.class)
class AdditionalTestMethodParameterAcceptanceTest {

    @DataProvider
    static Object[][] dataProviderObjectArrayArray() {
        // @formatter:off
        return new Object[][] {
                {   0,   1, "0, 1" },
                { 'a', 'b', "a, b" },
            };
        // @formatter:on
    }

    @TestTemplate
    @UseDataProvider
    void testObjectArrayArray(Object a, Object b, String expected, TestInfo testInfo) {
        // Expect:
        assertThat(testInfo.getDisplayName()).contains(expected);
    }

    @DataProvider
    static Object[] dataProviderObjectArray() {
        // @formatter:off
        return new Object[] {
                'a',
                "a",
            };
        // @formatter:on
    }

    @TestTemplate
    @UseDataProvider
    void testObjectArray(Object a, TestInfo testInfo) {
        // Expect:
        assertThat(testInfo.getDisplayName()).contains("a");
    }

    @TestTemplate
    // @formatter:off
    @DataProvider(value = {
            "2 | 3 | 2, 3",
            "c | d | c, d",
        }, splitBy = "\\|")
    // @formatter:on
    void testThree(String a, String b, String expected, TestInfo testInfo) {
        // Expect:
        assertThat(testInfo.getDisplayName()).contains(expected);
    }
}
