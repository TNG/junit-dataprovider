package com.tngtech.test.java.junit.dataprovider.custom;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.internal.convert.StringConverter;

public class DateTimeAwareStringConverter extends StringConverter {

    public static final DateFormat DATE_TIME_ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS z");
    public static final DateFormat DATE_ISO = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    protected Object customConvertValue(String str, Class<?> targetType, DataProvider dataProvider) {
        if (Date.class.equals(targetType)) {
            try {
                return DATE_TIME_ISO.parse(str);
            } catch (ParseException e) {
                // ignore
            }
            try {
                return DATE_ISO.parse(str);
            } catch (ParseException e) {
                // ignore
            }
        }
        return super.customConvertValue(str, targetType, dataProvider);
    }
}
