package com.tngtech.test.junit.dataprovider;

import static com.tngtech.junit.dataprovider.DataProviders.$;
import static com.tngtech.junit.dataprovider.DataProviders.$$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

@TestInstance(Lifecycle.PER_CLASS)
class NonStaticDataProviderAcceptanceTest {

    @DataProvider
    Object[][] dataProviderDivide() {
        // @formatter:off
        return $$(
                $(  0,   1,  0 ),
                $(  0,  -1,  0 ),
                $(  1,  1,   1 ),
                $(  1, -1,  -1 ),
                $(  2,  1,   2 )
        );
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider
    void testDivide(int a, int b, int expected) {
        // Given:

        // When:
        int result = a / b;

        // Then:
        assertThat(result).isEqualTo(expected);
    }

    @TestInstance(Lifecycle.PER_CLASS)
    @Nested
    class NestedTests {

        @DataProvider
        Object[][] dataProviderPow() {
            // @formatter:off
            return $$(
                    $( 0,  0 ),
                    $( 1,  1 ),
                    $( 2,  4 ),
                    $( 4, 16 )
            );
            // @formatter:on
        }

        @ParameterizedTest
        @UseDataProvider
        void testPow(int a, int expected) {
            // Given:

            // When:
            int result = a * a;

            // Then:
            assertThat(result).isEqualTo(expected);
        }
    }
}
