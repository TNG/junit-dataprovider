package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(UseDataProviderExtension.class)
class CacheDataProviderResultsGloballyAcceptanceTest {

    @TestTemplate
    @UseDataProvider(value = "dataProviderCachedDataProviderResults", location = CacheDataProviderResultsAcceptanceTest.class)
    void testCachedDataProviderResultsOne(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(1);
    }

    @TestTemplate
    @UseDataProvider(value = "dataProviderCachedDataProviderResults", location = CacheDataProviderResultsAcceptanceTest.class)
    void testCachedDataProviderResultsTwo(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(1);
    }

    @TestTemplate
    @UseDataProvider(value = "dataProviderDoNotCacheDataProviderResults", location = CacheDataProviderResultsAcceptanceTest.class)
    public void testDoNotCacheDataProviderResultsOne(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(
                CacheDataProviderResultsAcceptanceTest.noOfTestsCallsUsingNotCachedDataProvider.incrementAndGet());
    }

    @TestTemplate
    @UseDataProvider(value = "dataProviderDoNotCacheDataProviderResults", location = CacheDataProviderResultsAcceptanceTest.class)
    public void testDoNotCacheCachedDataProviderResultsTwo(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(
                CacheDataProviderResultsAcceptanceTest.noOfTestsCallsUsingNotCachedDataProvider.incrementAndGet());
    }
}