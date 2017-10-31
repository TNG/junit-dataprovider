package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(UseDataProviderExtension.class)
class ListDataProviderAcceptanceTest {

    @DataProvider
    static List<List<Object>> dataProviderNumberFormat() {
        List<List<Object>> result = new ArrayList<>();
        List<Object> first = new ArrayList<>();
        first.add(Integer.valueOf(101));
        first.add("%5d");
        first.add("  101");
        result.add(first);
        List<Object> second = new ArrayList<>();
        second.add(125);
        second.add("%06d");
        second.add("000125");
        result.add(second);
        return result;
    }

    @TestTemplate
    @UseDataProvider("dataProviderNumberFormat")
    void testNumberFormat(Number number, String format, String expected) {
        // Given:

        // When:
        String result = String.format(format, number);

        // Then:
        assertThat(result).isEqualTo(expected);
    }

    @DataProvider
    static List<? extends Number> dataProviderIsNumber() {
        List<Number> result = new ArrayList<>();
        result.add(101);
        result.add(125L);
        result.add(125.0);
        return result;
    }

    @TestTemplate
    @UseDataProvider
    void testIsNumber(Number number) {
        // Expect:
        assertThat(number).isInstanceOf(Number.class);
    }
}
