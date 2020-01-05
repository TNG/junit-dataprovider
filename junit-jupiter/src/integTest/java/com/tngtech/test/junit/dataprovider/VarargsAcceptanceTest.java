package com.tngtech.test.junit.dataprovider;

import static com.tngtech.junit.dataprovider.DataProviders.$;
import static com.tngtech.junit.dataprovider.DataProviders.$$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.DataProviderExtension;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(DataProviderExtension.class)
@ExtendWith(UseDataProviderExtension.class)
class VarargsAcceptanceTest {

    @DataProvider
    static Object[][] dataProviderLongVarargs() {
        // @formatter:off
        return new Object[][] {
                {},
                { new Long[0] },
                { 1L },
                { 111L, 222L, 333L },
        };
        // @formatter:on
    }

    @TestTemplate
    @UseDataProvider("dataProviderLongVarargs")
    void testLongVarargs(Long... longs) {
        for (Long l : longs) {
            assertNotNull(l);
        }
    }

    // @formatter:off
    @TestTemplate
    @DataProvider({
            "",
            "a",
            "x, y, z",
        })
    // @formatter:on
    void testStringVarargs(String... strings) {
        for (String s : strings) {
            assertNotNull(s);
        }
    }

    @DataProvider
    static Object[][] dataProviderIntVarargsWithLeadingCharArgument() {
        // @formatter:off
        return $$(
                $('a'),
                $('b', 0),
                $('c', 1, 2, 3)
            );
        // @formatter:on
    }

    @TestTemplate
    @UseDataProvider("dataProviderIntVarargsWithLeadingCharArgument")
    void testIntVarargsWithLeadingCharArgument(char c, int... is) {
        assertThat(c).isNotNull();
        for (int i : is) {
            assertThat(i).isNotNegative();
        }
    }

    // @formatter:off
    @TestTemplate
    @DataProvider({
            "0",
            "1, a",
            "3, aa, bb, cc",
        })
    // @formatter:on
    void testStringVarargsWithLeadingSizeArgument(int i, String... strings) {
        assertNotNull(strings);
        assertThat(strings).hasSize(i);
        for (String s : strings) {
            assertNotNull(s);
        }
    }
}
