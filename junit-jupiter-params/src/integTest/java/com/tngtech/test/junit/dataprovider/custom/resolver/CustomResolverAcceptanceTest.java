package com.tngtech.test.junit.dataprovider.custom.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class CustomResolverAcceptanceTest {

    private static AtomicInteger counterOne;
    private static AtomicInteger counterTwo;

    @BeforeAll
    static void setupClass() {
        counterOne = new AtomicInteger(0);
        counterTwo = new AtomicInteger(0);
    }

    @AfterAll
    static void tearDownClass() {
        assertThat(counterOne.get()).isEqualTo(6);
        assertThat(counterTwo.get()).isEqualTo(6);
    }

    @DataProvider
    static Object[][] testNumberA() {
        // @formatter:off
        return new Object[][] {
            { (byte) 1 },
        };
        // @formatter:on
    }

    @DataProvider
    static Iterable<Set<Integer>> testNumberB() {
        // @formatter:off
        return Arrays.asList(
                Collections.singleton(2),
                Collections.singleton(Integer.valueOf(3))
                );
        // @formatter:on
    }

    @DataProvider
    static Set<Number> testNumberC() {
        Set<Number> result = new LinkedHashSet<>();
        result.add(4);
        result.add(5L);
        result.add(6.0);
        return result;
    }

    @ParameterizedTest
    @UseDataProvider(resolver = DataProviderStartWithTestMethodNameResolver.class)
    void testNumber(Number number) {
        // When:
        int count = counterOne.incrementAndGet();

        // Then:
        assertThat(count).isEqualTo(number.intValue());
    }

    @ParameterizedTest
    @CustomResolverUseDataProvider
    void testNumber(double d) {
        // When:
        double count = counterTwo.incrementAndGet();

        // Then:
        assertThat(count).isEqualTo(d);
    }
}
