package com.tngtech.junit.dataprovider.format;

import java.lang.reflect.Method;
import java.util.List;

/** Provides the format for a dataprovider test using test method, invocation index and arguments list. */
public interface DataProviderTestNameFormatter {

    /**
     * Method formatting the given arguments to provide a test method name.
     *
     * @param testMethod the test method to be executed
     * @param invocationIndex the index within one dataprovider
     * @param arguments the arguments used to executed the test method
     * @return a formatted test method name
     */
    String format(Method testMethod, int invocationIndex, List<Object> arguments);
}
