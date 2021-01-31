package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class SingleArgumentDataProviderAcceptanceTest {

    @DataProvider
    static Object[] dataProviderSingleArgObjectArray() {
        // @formatter:off
        return new Object[] {
            0,
            1,
            123,
        };
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider("dataProviderSingleArgObjectArray")
    void testSingleArgObjectArray(long l) {
        // Expected:
        assertThat(l).isNotEqualTo(1234);
    }

    @DataProvider
    static List<Object> dataProviderSingleArgListOfObject() {
        return Arrays.asList(null, "", "1", "123");
    }

    @ParameterizedTest
    @UseDataProvider("dataProviderSingleArgListOfObject")
    void testSingleArgListOfObject(String string) {
        // Expected:
        assertThat(string).isNotEqualTo("1234");
    }

    @DataProvider
    static Iterable<String> dataProviderSingleArgIterableOfString() {
        Set<String> result = new HashSet<>();
        result.add(null);
        result.add("");
        result.add("1");
        result.add("123");
        return result;
    }

    @ParameterizedTest
    @UseDataProvider
    void testSingleArgIterableOfString(String string) {
        // Expected:
        assertThat(string).isNotEqualTo("1234");
    }

    @DataProvider
    public static List<UnaryOperator<String>> listOfUnaryOperator() {
        return Collections.singletonList((string) -> "merged" + string);
    }

    @ParameterizedTest
    @UseDataProvider("listOfUnaryOperator")
    public void testListOfUnaryOperator(UnaryOperator<String> operator) {
        // Expected:
        assertThat(operator.apply("test")).isEqualTo("mergedtest");
    }
}
