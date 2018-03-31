package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;
import com.tngtech.junit.dataprovider.format.DataProviderTestNameFormatter;

@ExtendWith(UseDataProviderExtension.class)
class FormatAcceptanceTest {

    static class PlusTestNameFormatter implements DataProviderTestNameFormatter {
        @Override
        public String format(Method testMethod, int invocationIndex, List<Object> arguments) {
            return String.format("%s: %2d + %2d = %2d", testMethod.getName(), arguments.get(0), arguments.get(1),
                    arguments.get(2));
        }
    }

    @DataProvider(format = "%a[0] * %a[1] == %a[2]", formatter = PlusTestNameFormatter.class) // format is ignored
    static Object[][] dataProviderPlus() {
        // @formatter:off
        return new Object[][] {
            {  0,  0,  0 },
            { -1,  0, -1 },
            {  0,  1,  1 },
            {  1,  1,  2 },
            {  1, -1,  0 },
            { -1, -1, -2 },
        };
        // @formatter:on
    }

    @TestTemplate
    @UseDataProvider
    void testPlus(int a, int b, int expected) {
        // Expect:
        assertThat(a + b).isEqualTo(expected);
    }

    @DataProvider(format = "%a[0] * %a[1] == %a[2]")
    static Object[][] dataProviderMultiply() {
        // @formatter:off
        return new Object[][] {
                {  0,  0,  0 },
                { -1,  0,  0 },
                {  0,  1,  0 },
                {  1,  1,  1 },
                {  1, -1, -1 },
                { -1, -1,  1 },
                {  1,  2,  2 },
                { -1,  2, -2 },
                { -1, -2,  2 },
                { -1, -2,  2 },
                {  6,  7, 42 },
            };
        // @formatter:on
    }

    @TestTemplate
    @UseDataProvider("dataProviderMultiply")
    void testMultiply(int a, int b, int expected) {
        // Expect:
        assertThat(a * b).isEqualTo(expected);
    }

    @DataProvider(format = "[%i] %na[0..-1]")
    static Object[][] dataProviderDivide() {
        // @formatter:off
        return new Object[][] {
            {  0,  1,  0 },
            {  1,  1,  1 },
            { -1,  1, -1 },
            {  2,  1,  2 },
            { 15,  3,  5 },
        };
        // @formatter:on
    }

    @TestTemplate
    @UseDataProvider
    void testDivide(int dividend, int divisor, int result, TestInfo testInfo) {
        // Expect:
        assertThat(dividend / divisor).isEqualTo(result);
        assertThat(testInfo.getDisplayName())
                .endsWith(String.format("dividend=%d, divisor=%d, result=%d", dividend, divisor, result));
    }
}