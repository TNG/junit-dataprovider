package com.tngtech.test.junit.dataprovider.custom.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class CustomStringConverterAcceptanceTest {

    // @formatter:off
    @ParameterizedTest
    @DataProvider(value = {
            "2016-02-19                  | 2016 | 02 | 19 | 00 | 00 | 00 | 000 | UTC",
            "2016-02-19T20:15:22.629 GMT | 2016 | 02 | 19 | 20 | 15 | 22 | 629 | UTC",
        }, splitBy = "\\|", stringConverter = DateTimeAwareStringConverter.class)
    // @formatter:off
    void testDateTimeDirectAnnotation(Date date, int year, int month, int dayOfMonth, int hourOfDay, int minute,
            int second, int millis, String timeZone) {
        // Expect:
        assertThat(date).isEqualTo(date(year, month, dayOfMonth, hourOfDay, minute, second, millis, timeZone));
    }

    // @formatter:off
    @ParameterizedTest
    @CustomConverterDataProvider(value = {
            "2016-02-19                  | 2016 | 02 | 19 | 00 | 00 | 00 | 000 | UTC",
            "2016-02-19T20:15:22.629 GMT | 2016 | 02 | 19 | 20 | 15 | 22 | 629 | UTC",
        }, splitBy = "\\|", trimValues = true)
    // @formatter:off
    void testDateTimeDirectMetaAnnotation(Date date, int year, int month, int dayOfMonth, int hourOfDay, int minute,
            int second, int millis, String timeZone) {
        // Expect:
        assertThat(date).isEqualTo(date(year, month, dayOfMonth, hourOfDay, minute, second, millis, timeZone));
    }

    @DataProvider(splitBy = "\\|", trimValues = true, stringConverter = DateTimeAwareStringConverter.class)
    static String[] dateTimeMetaAnnotationAndDataProviderMethodProvider() {
        // @formatter:off
        return new String[] {
            "2016-02-19                  | 2016 | 02 | 19 | 00 | 00 | 00 | 000 | UTC",
            "2016-02-19T20:15:22.629 GMT | 2016 | 02 | 19 | 20 | 15 | 22 | 629 | UTC",
        };
        // @formatter:off
    }

    @ParameterizedTest
    @UseDataProvider("dateTimeMetaAnnotationAndDataProviderMethodProvider")
    void testDateTimeAnnotationAndDataProviderMethod(Date date, int year, int month, int dayOfMonth, int hourOfDay,
            int minute, int second, int millis, String timeZone) {
        // Expect:
        assertThat(date).isEqualTo(date(year, month, dayOfMonth, hourOfDay, minute, second, millis, timeZone));
    }

    private Date date(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, int millis, String timeZone) {
        GregorianCalendar calendar = new GregorianCalendar(year, month - 1, dayOfMonth, hourOfDay, minute, second);
        calendar.set(Calendar.MILLISECOND, millis);
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        return calendar.getTime();
    }
}
