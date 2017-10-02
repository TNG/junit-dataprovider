package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(UseDataProviderExtension.class)
class CachedDataProviderResultsAcceptanceTest {

    private static final AtomicInteger noOfDataProviderCalls = new AtomicInteger(0);

    @DataProvider
    static Object[][] dataProviderCachedDataProviderResults() {
        // @formatter:off
        return new Object[][] {
            { noOfDataProviderCalls.incrementAndGet() },
        };
        // @formatter:on
    }

    @TestTemplate
    @UseDataProvider("dataProviderCachedDataProviderResults")
    void testCachedDataProviderResultsOne(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(1);
    }

    @TestTemplate
    @UseDataProvider("dataProviderCachedDataProviderResults")
    void testCachedDataProviderResultsTwo(int noOfDataProvderCalls) {
        // Expected:
        assertThat(noOfDataProvderCalls).isEqualTo(1);
    }
}