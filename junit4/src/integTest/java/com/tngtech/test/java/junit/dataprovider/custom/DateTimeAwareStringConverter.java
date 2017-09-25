package com.tngtech.test.java.junit.dataprovider.custom;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.internal.convert.StringConverter;

public class DateTimeAwareStringConverter extends StringConverter {

    private final DateFormat dateTimeIso;
    private final DateFormat dateIso;

    public DateTimeAwareStringConverter() {
        dateTimeIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS z");
        dateIso = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    protected Object customConvertValue(String str, Class<?> targetType, DataProvider dataProvider) {
        if (Date.class.equals(targetType)) {
            try {
                return dateTimeIso.parse(str);
            } catch (ParseException e) {
                // ignore
            }
            try {
                return dateIso.parse(str);
            } catch (ParseException e) {
                // ignore
            }
        }
        return super.customConvertValue(str, targetType, dataProvider);
    }
}
