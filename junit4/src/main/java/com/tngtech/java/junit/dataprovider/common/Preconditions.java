package com.tngtech.java.junit.dataprovider.common;

/**
 * @deprecated Use {@link com.tngtech.junit.dataprovider.Preconditions} from {@code junit-dataprovider-core} instead.
 *             Only the package name need to be changed as the semantics of all methods is remained equal. This class
 *             will be removed in version 3.0.
 */
@Deprecated
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
