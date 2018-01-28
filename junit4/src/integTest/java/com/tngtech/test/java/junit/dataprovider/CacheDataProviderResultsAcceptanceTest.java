package com.tngtech.test.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class CacheDataProviderResultsAcceptanceTest {

    // works if test discovery order is equal to execution order
    public static final AtomicInteger noOfTestsCallsUsingNotCachedDataProvider = new AtomicInteger(0);

    private static final AtomicInteger noOfCachedDataProviderCalls = new AtomicInteger(0);
    private static final AtomicInteger noOfNotCachedDataProviderCalls = new AtomicInteger(0);

    @DataProvider
    public static Object[][] dataProviderCachedDataProviderResults() {
        // @formatter:off
        return new Object[][] {
            { noOfCachedDataProviderCalls.incrementAndGet() },
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

    @DataProvider(cache = false)
    public static Object[][] dataProviderDoNotCacheDataProviderResults() {
        // @formatter:off
        return new Object[][] {
            { noOfNotCachedDataProviderCalls.incrementAndGet() },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderDoNotCacheDataProviderResults")
    public void testDoNotCacheDataProviderResultsOne(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(noOfTestsCallsUsingNotCachedDataProvider.incrementAndGet());
    }

    @Test
    @UseDataProvider("dataProviderDoNotCacheDataProviderResults")
    public void testDoNotCacheCachedDataProviderResultsTwo(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(noOfTestsCallsUsingNotCachedDataProvider.incrementAndGet());
    }
}