package com.tngtech.test.junit.dataprovider;

import static com.tngtech.junit.dataprovider.DataProviders.$;
import static com.tngtech.junit.dataprovider.DataProviders.$$;
import static org.assertj.core.api.Assertions.assertThat;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(UseDataProviderExtension.class)
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

    @TestTemplate
    @UseDataProvider
    void testDivide(int a, int b, int expected) {
        // Given:

        // When:
        int result = a / b;

        // Then:
        assertThat(result).isEqualTo(expected);
    }

    @SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC", justification = "Must be non-static for JUnit Jupiter")
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

        @TestTemplate
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
