package com.tngtech.test.java.junit.dataprovider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExternalFile {

    public enum Format {
        CSV,
        XML,
        XLS
    }

    Format format();

    String value();
}
