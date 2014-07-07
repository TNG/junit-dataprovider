package com.tngtech.test.java.junit.dataprovider;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExternalFile {

  public enum Format {
    CSV, XML, XLS
  }

  Format format();
  String value();
}
