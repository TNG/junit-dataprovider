package com.tngtech.test.java.junit.dataprovider;

import static com.tngtech.java.junit.dataprovider.DataProviders.$;
import static com.tngtech.java.junit.dataprovider.DataProviders.$$;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderVarargsAcceptanceTest {

    @DataProvider
    public static Object[][] dataProviderVarargs() {
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
    @UseDataProvider("dataProviderVarargs")
    public void testVarargs(Long... longs) {
        for (Long l : longs) {
            assertNotNull(l);
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
    public void testVarargs2(String... strings) {
        for (String s : strings) {
            assertNotNull(s);
        }
    }

    @DataProvider
    public static Object[][] dataProviderVarargs3() {
        // @formatter:off
        return $$(
                $('a'),
                $('b', 0),
                $('c', 1, 2, 3)
            );
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderVarargs3")
    public void testVarargs3(char c, int... is) {
        assertNotNull(c);
        for (int i : is) {
            assertNotNull(i);
        }
    }

    @Test
    // @formatter:off
    @DataProvider({
            "a",
            "b, 0",
            "c, 1, 2, 3",
        })
    // @formatter:on
    public void testVarargs4(String s, Integer... is) {
        assertNotNull(s);
        for (Integer i : is) {
            assertNotNull(i);
        }
    }
}
