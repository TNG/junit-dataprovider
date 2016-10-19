package com.tngtech.test.java.junit.dataprovider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderSingleArgAcceptanceTest {

    @DataProvider
    public static Object[] dataProviderSingleArgObjectArray() {
        // @formatter:off
        return new Object[] {
            null,
            "",
            "1",
            "123",
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderSingleArgObjectArray")
    public void testSingleArgObjectArray(String string) {
        // Check output within IDE
    }

    @DataProvider
    public static List<Object> dataProviderSingleArgListOfObject() {
        return Arrays.<Object> asList(null, "", "1", "123");
    }

    @Test
    @UseDataProvider("dataProviderSingleArgListOfObject")
    public void testSingleArgListOfObject(String string) {
        // Check output within IDE
    }

    @DataProvider
    public static Iterable<String> dataProviderSingleArgIterableOfString() {
        Set<String> result = new HashSet<String>();
        result.add(null);
        result.add("");
        result.add("1");
        result.add("123");
        return result;
    }

    @Test
    @UseDataProvider
    public void testSingleArgIterableOfString(String string) {
        // Check output within IDE
    }
}