package com.tngtech.test.junit.dataprovider.override;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(UseDataProviderExtension.class)
class ImplementingAcceptanceTest implements AcceptanceTestInterface<Integer> {

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

    @TestTemplate
    @UseDataProvider
    @Override
    public void testToBeImplemented(Integer value) {
        assertThat(value).isEqualTo(noOfDataProviderTestsCalls.incrementAndGet());
    }
}
