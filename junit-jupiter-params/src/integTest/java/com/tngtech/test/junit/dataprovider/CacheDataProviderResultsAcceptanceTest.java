package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class CacheDataProviderResultsAcceptanceTest {

    // works if test discovery order is equal to execution order
    public static final AtomicInteger noOfTestsCallsUsingNotCachedDataProvider = new AtomicInteger(0);

    private static final AtomicInteger noOfCachedDataProviderCalls = new AtomicInteger(0);
    private static final AtomicInteger noOfNotCachedDataProviderCalls = new AtomicInteger(0);

    @DataProvider
    static Object[][] dataProviderCachedDataProviderResults() {
        // @formatter:off
        return new Object[][] {
            { noOfCachedDataProviderCalls.incrementAndGet() },
        };
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider("dataProviderCachedDataProviderResults")
    void testCachedDataProviderResultsOne(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(1);
    }

    @ParameterizedTest
    @UseDataProvider("dataProviderCachedDataProviderResults")
    void testCachedDataProviderResultsTwo(int noOfDataProvderCalls) {
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

    @ParameterizedTest
    @UseDataProvider("dataProviderDoNotCacheDataProviderResults")
    public void testDoNotCacheDataProviderResultsOne(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(noOfTestsCallsUsingNotCachedDataProvider.incrementAndGet());
    }

    @ParameterizedTest
    @UseDataProvider("dataProviderDoNotCacheDataProviderResults")
    public void testDoNotCacheCachedDataProviderResultsTwo(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(noOfTestsCallsUsingNotCachedDataProvider.incrementAndGet());
    }
}