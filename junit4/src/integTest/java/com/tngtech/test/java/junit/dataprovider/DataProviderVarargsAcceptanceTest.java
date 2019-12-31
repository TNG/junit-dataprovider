package com.tngtech.test.java.junit.dataprovider;

import static com.tngtech.java.junit.dataprovider.DataProviders.$;
import static com.tngtech.java.junit.dataprovider.DataProviders.$$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderVarargsAcceptanceTest {

    @DataProvider
    public static Object[][] dataProviderLongVarargs() {
        // @formatter:off
        return new Object[][] {
                {},
                { new Long[0] },
                { 1L },
                { 111L, 222L, 333L },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderLongVarargs")
    public void testLongVarargs(Long... longs) {
        for (Long l : longs) {
            assertThat(l).isNotNull();
        }
    }

    @Test
    // @formatter:off
    @DataProvider({
            "",
            "a",
            "x, y, z",
        })
    // @formatter:on
    public void testStringVarargs(String... strings) {
        for (String s : strings) {
            assertThat(s).isNotNull();
        }
    }

    @DataProvider
    public static Object[][] dataProviderIntVarargsWithLeadingCharArgument() {
        // @formatter:off
        return $$(
                $('a'),
                $('b', 0),
                $('c', 1, 2, 3)
            );
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderIntVarargsWithLeadingCharArgument")
    public void testIntVarargsWithLeadingCharArgument(char c, int... is) {
        assertThat(c).isNotNull();
        for (int i : is) {
            assertThat(i).isNotNull();
        }
    }

    @Test
    // @formatter:off
    @DataProvider({
            "0",
            "1, a",
            "3, aa, bb, cc",
        })
    // @formatter:on
    public void testStringVarargsWithLeadingSizeArgument(int i, String... strings) {
        assertThat(strings).hasSize(i);
        for (String s : strings) {
            assertThat(s).isNotNull();
        }
    }
}
