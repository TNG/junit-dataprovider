package com.tngtech.java.junit.dataprovider.common;

public class Preconditions {

    public static <T> T checkNotNull(T object, String errorMessage) {
        return com.tngtech.junit.dataprovider.Preconditions.checkNotNull(object, errorMessage);
    }

    public static void checkArgument(boolean expression, String errorMessage) {
        com.tngtech.junit.dataprovider.Preconditions.checkArgument(expression, errorMessage);
    }

    public static void checkArgument(boolean expression, String errorMessageFormat, Object... errorMessageArgs) {
        com.tngtech.junit.dataprovider.Preconditions.checkArgument(expression, errorMessageFormat,
                errorMessageArgs);
    }
}
