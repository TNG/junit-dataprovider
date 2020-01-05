package com.tngtech.test.junit.dataprovider;

import static com.tngtech.junit.dataprovider.DataProviders.$;
import static com.tngtech.junit.dataprovider.DataProviders.$$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class NestedAcceptanceTest {

    private int zero = -1;

    @BeforeEach
    public void setup() {
        zero = 0;
    }

    @DataProvider
    static Object[][] dataProviderAdd() {
        //@formatter:off
        return new Object[][] {
            {  0,  0,  0 },
            {  0,  1,  1 },
            {  1,  0,  1 },
            {  1,  1,  2 },

            {  0, -1, -1 },
            { -1, -1, -2 },
        };
        //@formatter:on
    }

    @ParameterizedTest
    @UseDataProvider
    void testAdd(int a, int b, int expected) {
        // Expect:
        assertThat(a + b).isEqualTo(expected);

        assertThat(zero).isEqualTo(0);
    }

    @DataProvider
    static Object[][] dataProviderMinus() {
        // @formatter:off
        return $$(
                $(  0,  0,  0 ),
                $(  0,  1, -1 ),
                $(  0, -1,  1 ),
                $(  1,  0,  1 ),
                $(  1,  1,  0 ),
                $( -1,  0, -1 ),
                $( -1, -1,  0 )
                );
        // @formatter:on
    }

    @Nested
    class NestedTests {
        @ParameterizedTest
        @UseDataProvider(location = NestedAcceptanceTest.class)
        void testMinus(long a, long b, long expected) {
            // When:
            long result = a - b;

            // Then:
            assertThat(result).isEqualTo(expected);
            assertThat(zero).isEqualTo(0);
        }
    }
}
