package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class FormatAcceptanceTest {

    @DataProvider // TODO original: (format = "%a[0] * %a[1] == %a[2]")
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

    @ParameterizedTest(name = "{0} * {1} == {2}")
    @UseDataProvider("dataProviderMultiply")
    void testMultiply(int a, int b, int expected) {
        // Expect:
        assertThat(a * b).isEqualTo(expected);
    }
}