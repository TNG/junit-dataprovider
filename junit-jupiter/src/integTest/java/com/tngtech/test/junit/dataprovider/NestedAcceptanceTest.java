package com.tngtech.test.junit.dataprovider;

import static com.tngtech.junit.dataprovider.DataProviders.$;
import static com.tngtech.junit.dataprovider.DataProviders.$$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(UseDataProviderExtension.class)
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

    @TestTemplate
    @UseDataProvider
    void testAdd(int a, int b, int expected) throws Exception {
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
        @TestTemplate
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
