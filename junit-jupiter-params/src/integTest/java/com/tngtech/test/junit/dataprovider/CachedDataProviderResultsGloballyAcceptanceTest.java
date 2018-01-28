package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.UseDataProvider;

class CachedDataProviderResultsGloballyAcceptanceTest {

    @ParameterizedTest
    @UseDataProvider(value = "dataProviderCachedDataProviderResults", location = CacheDataProviderResultsAcceptanceTest.class)
    void testCachedDataProviderResultsOne(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(1);
    }

    @ParameterizedTest
    @UseDataProvider(value = "dataProviderCachedDataProviderResults", location = CacheDataProviderResultsAcceptanceTest.class)
    void testCachedDataProviderResultsTwo(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(1);
    }

    @ParameterizedTest
    @UseDataProvider(value = "dataProviderDoNotCacheDataProviderResults", location = CacheDataProviderResultsAcceptanceTest.class)
    public void testDoNotCacheDataProviderResultsOne(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(
                CacheDataProviderResultsAcceptanceTest.noOfTestsCallsUsingNotCachedDataProvider.incrementAndGet());
    }

    @ParameterizedTest
    @UseDataProvider(value = "dataProviderDoNotCacheDataProviderResults", location = CacheDataProviderResultsAcceptanceTest.class)
    public void testDoNotCacheCachedDataProviderResultsTwo(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(
                CacheDataProviderResultsAcceptanceTest.noOfTestsCallsUsingNotCachedDataProvider.incrementAndGet());
    }
}