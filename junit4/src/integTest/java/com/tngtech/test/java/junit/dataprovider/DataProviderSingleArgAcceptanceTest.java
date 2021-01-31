package com.tngtech.test.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderSingleArgAcceptanceTest {

    @DataProvider
    public static Object[] dataProviderSingleArgObjectArray() {
        // @formatter:off
        return new Object[] {
            0,
            1,
            123,
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderSingleArgObjectArray")
    public void testSingleArgObjectArray(long l) {
        // Expected:
        assertThat(l).isNotEqualTo(1234);
    }

    @DataProvider
    public static List<Object> dataProviderSingleArgListOfObject() {
        return Arrays.<Object> asList(null, "", "1", "123");
    }

    @Test
    @UseDataProvider("dataProviderSingleArgListOfObject")
    public void testSingleArgListOfObject(String string) {
        // Expected:
        assertThat(string).isNotEqualTo("1234");
    }

    @DataProvider
    public static Iterable<String> dataProviderSingleArgIterableOfString() {
        Set<String> result = new HashSet<String>();
        result.add(null);
        result.add("");
        result.add("1");
        result.add("123");
        return result;
    }

    @Test
    @UseDataProvider
    public void testSingleArgIterableOfString(String string) {
        // Expected:
        assertThat(string).isNotEqualTo("1234");
    }

    public interface UnaryOperator<T> {
        T apply(T arg);
    }

    @DataProvider
    public static List<UnaryOperator<String>> dataProviderListOfUnaryOperator() {
        return Collections.<UnaryOperator<String>>singletonList(new UnaryOperator<String>() {
            @Override
            public String apply(String arg) {
                return "merged" + arg;
            }
        });
    }

    @Test
    @UseDataProvider
    public void testListOfUnaryOperator(UnaryOperator<String> operator) {
        // Expected:
        assertThat(operator.apply("test")).isEqualTo("mergedtest");
    }
}
