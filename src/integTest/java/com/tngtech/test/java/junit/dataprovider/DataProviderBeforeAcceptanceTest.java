package com.tngtech.test.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderBeforeAcceptanceTest {

    private Object field;

    @Before
    public void setup() {
        field = Integer.valueOf(1);
    }

    @DataProvider
    public static Object[][] dataProviderFieldIsEqualTo() {
        // @formatter:off
        return new Object[][] {
            { 1 },
            { Integer.valueOf(1) },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider
    public void testFieldIsEqualTo(Object obj) {
        // Expect:
        assertThat(field).isEqualTo(obj);
    }

    @DataProvider
    public static Object[][] dataProviderFieldIsNotEqualTo() {
        // @formatter:off
        return new Object[][] {
                { null },
                { "" },
                { Integer.valueOf(2) },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider
    public void testFieldIsNotEqualTo(Object obj) {
        // Expect:
        assertThat(field).isNotEqualTo(obj);
    }
}
