package com.tngtech.test.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class CachedDataProviderResultsAcceptanceTest {

    private static final AtomicInteger noOfDataProviderCalls = new AtomicInteger(0);

    @DataProvider
    public static Object[][] dataProviderCachedDataProviderResults() {
        // @formatter:off
        return new Object[][] {
            { noOfDataProviderCalls.incrementAndGet() },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderCachedDataProviderResults")
    public void testCachedDataProviderResultsOne(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(1);
    }

    @Test
    @UseDataProvider("dataProviderCachedDataProviderResults")
    public void testCachedDataProviderResultsTwo(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(1);
    }
}