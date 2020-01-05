package com.tngtech.test.junit.dataprovider.custom.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.DataProviderExtension;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

class CustomStringConverterAcceptanceTest {

    // @formatter:off
    @TestTemplate
    @ExtendWith(DataProviderExtension.class)
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
    @TestTemplate
    @ExtendWith(CustomConverterDataProviderExtension.class)
    @CustomConverterDataProvider(value = {
            "2016-02-19                  | 2016 | 02 | 19 | 00 | 00 | 00 | 000 | UTC",
            "2016-02-19T20:15:22.629 GMT | 2016 | 02 | 19 | 20 | 15 | 22 | 629 | UTC",
        }, splitBy = "\\|")
    // @formatter:off
    void testDateTimeDirectMetaAnnotation(Date date, int year, int month, int dayOfMonth, int hourOfDay, int minute,
            int second, int millis, String timeZone) {
        // Expect:
        assertThat(date).isEqualTo(date(year, month, dayOfMonth, hourOfDay, minute, second, millis, timeZone));
    }

    @DataProvider(splitBy = "\\|", stringConverter = DateTimeAwareStringConverter.class)
    static String[] dateTimeMetaAnnotationAndDataProviderMethodProvider() {
        // @formatter:off
        return new String[] {
            "2016-02-19                  | 2016 | 02 | 19 | 00 | 00 | 00 | 000 | UTC",
            "2016-02-19T20:15:22.629 GMT | 2016 | 02 | 19 | 20 | 15 | 22 | 629 | UTC",
        };
        // @formatter:off
    }

    @TestTemplate
    @ExtendWith(UseDataProviderExtension.class)
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
