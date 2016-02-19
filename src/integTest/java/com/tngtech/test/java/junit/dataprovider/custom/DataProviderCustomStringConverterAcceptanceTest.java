package com.tngtech.test.java.junit.dataprovider.custom;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;

@RunWith(CustomDataProviderRunner.class)
public class DataProviderCustomStringConverterAcceptanceTest {

    // @formatter:off
    @Test
    @DataProvider(value = {
        "2016-02-19                  | 2016 | 02 | 19 | 00 | 00 | 00 | 000 | UTC",
        "2016-02-19T20:15:22.629 GMT | 2016 | 02 | 19 | 20 | 15 | 22 | 629 | UTC",
    }, splitBy = "\\|", trimValues = true)
    // @formatter:off
    public void testDateTime(Date date, int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, int millis, String timeZone) {
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
