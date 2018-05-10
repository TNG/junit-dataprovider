package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class CacheDataProviderResultsAcceptanceTest {

    // works if test discovery order is equal to execution order
    static final AtomicInteger noOfTestsCallsUsingNotCachedDataProvider = new AtomicInteger(0);

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
    void testCachedDataProviderResultsOne(int noOfDataProviderCalls) {
        // Expected:
        assertThat(noOfDataProviderCalls).isEqualTo(1);
    }

    @ParameterizedTest
    @UseDataProvider("dataProviderCachedDataProviderResults")
    void testCachedDataProviderResultsTwo(int noOfDataProviderCalls) {
        // Expected:
        assertThat(noOfDataProviderCalls).isEqualTo(1);
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
    void testDoNotCacheDataProviderResultsOne(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(noOfTestsCallsUsingNotCachedDataProvider.incrementAndGet());
    }

    @ParameterizedTest
    @UseDataProvider("dataProviderDoNotCacheDataProviderResults")
    void testDoNotCacheCachedDataProviderResultsTwo(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(noOfTestsCallsUsingNotCachedDataProvider.incrementAndGet());
    }
}