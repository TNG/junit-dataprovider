package com.tngtech.test.junit.dataprovider;

import static com.tngtech.junit.dataprovider.DataProviders.$;
import static com.tngtech.junit.dataprovider.DataProviders.$$;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(UseDataProviderExtension.class)
class SimpleAcceptanceTest {

    @Test
    void testAddWithoutDataProvider() {
        // Given:

        // When:
        int result = 1 + 2;

        // Then:
        assertThat(result).isEqualTo(3);
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

    @TestTemplate
    @UseDataProvider("dataProviderMinus")
    void testMinus(long a, long b, long expected) {
        // Given:

        // When:
        long result = a - b;

        // Then:
        assertThat(result).isEqualTo(expected);
    }

    @DataProvider
    static Object[][] dataProviderWithNonConstantObjects() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        Calendar now = Calendar.getInstance();

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        // @formatter:off
        return new Object[][] {
                { yesterday,    yesterday,      false },
                { yesterday,    now,            true },
                { yesterday,    tomorrow,       true },

                { now,          yesterday,      false },
                { now,          now,            false },
                { now,          tomorrow,       true },

                { tomorrow,     yesterday,      false },
                { tomorrow,     now,            false },
                { tomorrow,     tomorrow,       false },
        };
        // @formatter:on
    }

    @TestTemplate
    @UseDataProvider("dataProviderWithNonConstantObjects")
    void testWithNonConstantObjects(Calendar cal1, Calendar cal2, boolean cal1IsEarlierThenCal2) {
        // Given:

        // When:
        boolean result = cal1.before(cal2);

        // Then:
        assertThat(result).isEqualTo(cal1IsEarlierThenCal2);
    }
}
