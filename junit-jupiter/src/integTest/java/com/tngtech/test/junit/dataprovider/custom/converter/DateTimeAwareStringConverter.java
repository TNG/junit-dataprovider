package com.tngtech.test.junit.dataprovider.custom.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.convert.StringConverter;

class DateTimeAwareStringConverter extends StringConverter {

    private final DateFormat dateTimeIso;
    private final DateFormat dateIso;

    DateTimeAwareStringConverter() {
        dateTimeIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS z");
        dateIso = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    protected Object customConvertValue(String str, Class<?> targetType, ConverterContext context) {
        if (Date.class.equals(targetType)) {
            try {
                return dateTimeIso.parse(str);
            } catch (@SuppressWarnings("unused") ParseException e) {
                // ignore
            }
            try {
                return dateIso.parse(str);
            } catch (@SuppressWarnings("unused") ParseException e) {
                // ignore
            }
        }
        return super.customConvertValue(str, targetType, context);
    }
}
