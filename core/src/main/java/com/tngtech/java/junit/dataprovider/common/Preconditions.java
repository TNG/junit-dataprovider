package com.tngtech.java.junit.dataprovider.common;

public class Preconditions {

    public static <T> T checkNotNull(T object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
        return object;
    }

    public static void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkArgument(boolean expression, String errorMessageFormat, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(errorMessageFormat, errorMessageArgs));
        }
    }
}
