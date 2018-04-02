package com.tngtech.test.java.junit.dataprovider.override;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderImplementingAcceptanceTest implements DataProviderAcceptanceTestInterface<Integer> {

    private static final AtomicInteger noOfNormalTestsCalls = new AtomicInteger(0);
    private static final AtomicInteger noOfDataProviderTestsCalls = new AtomicInteger(0);

    @DataProvider
    public static Object[][] dataProviderToBeImplemented() {
        // @formatter:off
        return new Object[][] {
                { 1 },
                { 2 },
        };
        // @formatter:on
    }

    @Test
    @Override
    public void testToBeImplemented() {
        assertThat(1).isEqualTo(noOfNormalTestsCalls.incrementAndGet());
    }

    @Test
    @UseDataProvider
    @Override
    public void testToBeImplemented(Integer value) {
        assertThat(value).isEqualTo(noOfDataProviderTestsCalls.incrementAndGet());
    }
}
