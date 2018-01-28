package com.tngtech.test.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class CacheDataProviderResultsGloballyAcceptanceTest {

    @Test
    @UseDataProvider(value = "dataProviderCachedDataProviderResults", location = CacheDataProviderResultsAcceptanceTest.class)
    public void testCachedDataProviderResultsOne(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(1);
    }

    @Test
    @UseDataProvider(value = "dataProviderCachedDataProviderResults", location = CacheDataProviderResultsAcceptanceTest.class)
    public void testCachedDataProviderResultsTwo(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(1);
    }

    @Test
    @UseDataProvider(value = "dataProviderDoNotCacheDataProviderResults", location = CacheDataProviderResultsAcceptanceTest.class)
    public void testDoNotCacheDataProviderResultsOne(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(
                CacheDataProviderResultsAcceptanceTest.noOfTestsCallsUsingNotCachedDataProvider.incrementAndGet());
    }

    @Test
    @UseDataProvider(value = "dataProviderDoNotCacheDataProviderResults", location = CacheDataProviderResultsAcceptanceTest.class)
    public void testDoNotCacheCachedDataProviderResultsTwo(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(
                CacheDataProviderResultsAcceptanceTest.noOfTestsCallsUsingNotCachedDataProvider.incrementAndGet());
    }
}