package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(UseDataProviderExtension.class)
class FormatAcceptanceTest {

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
    void testDivide(int dividend, int divisor, int result) {
        // Expect:
        assertThat(dividend / divisor).isEqualTo(result);
    }
}