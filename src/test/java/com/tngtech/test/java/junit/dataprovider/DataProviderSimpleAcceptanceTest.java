package com.tngtech.test.java.junit.dataprovider;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderSimpleAcceptanceTest {

    @Test
    public void testAddWithoutDataProvider() {
        // Given:

        // When:
        int result = 1 + 2;

        // Then:
        assertThat(result).isEqualTo(3);
    }

    @DataProvider
    public static Object[][] dataProviderStringIsNullOrEmpty() {
        // @formatter:off
        return new Object[][] {
                { null },
                { "" },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderStringIsNullOrEmpty")
    public void testIsEmptyString(String str) {
        // Given:

        // When:
        boolean isEmpty = (str == null) ? true : str.isEmpty();

        // Then:
        assertThat(isEmpty).isTrue();
    }

    @DataProvider
    public static Object[][] dataProviderAdd() {
        // @formatter:off
        return new Object[][] {
                { -1, -1, -2 },
                { -1,  0, -1 },
                {  0, -1, -1 },
                {  0,  0,  0 },
                {  0,  1,  1 },
                {  1,  0,  1 },
                {  1,  1,  2 },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderAdd")
    public void testAddWithDataProvider(int a, int b, int expected) {
        // Given:

        // When:
        int result = a + b;

        // Then:
        assertThat(result).isEqualTo(expected);
    }
}
