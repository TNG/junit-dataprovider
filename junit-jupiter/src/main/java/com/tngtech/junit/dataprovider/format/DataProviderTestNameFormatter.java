package com.tngtech.junit.dataprovider.format;

import java.lang.reflect.Method;
import java.util.List;

public interface DataProviderTestNameFormatter {

    String format(Method testMethod, int invocationIndex, List<Object> arguments);
}
