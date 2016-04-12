package com.tngtech.test.java.junit.dataprovider;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class DataProviderListArgAcceptanceTest {

    @DataProvider
    public static Object[][] dataProviderListArg() {
        // @formatter:off
        return new Object[][] {
            { Arrays.asList("a", "b"), "c" },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider
    public void testListArg(List<String> list, String string) {
        // Check output within IDE
    }

    @DataProvider
    public static Object[][] stringsData() {
        return new Object[][] {
                { Arrays.asList("string1", "stringValue"), "stringValue" }
        };
    }

    @Test
    @UseDataProvider("stringsData")
    public void test(List<String> strings, String expectedValue) {
    }
}