package com.tngtech.test.java.junit.dataprovider.override;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

public abstract class DataProviderAbstractAcceptanceBaseTest {
    @DataProvider
    public static Object[][] dataProviderBase() {
        return new Object[][] { { "1" } };
    }

    @Test
    @UseDataProvider
    public void testBase(@SuppressWarnings("unused") String one) {
        fail("should be overridden and therefore not fail");
    }

    // Does not work since v1.12.0 any more as new dataprovider resolver mechanism uses "Method#getDeclaringClass()"
    // instead of "testClass" (= JUnit4 Frameworks "TestClass"); Workaround by specifying "location" explicitly ...
    @Test
    @UseDataProvider(location = DataProviderOverridingAcceptanceTest.class)
    public void testChild(String one) {
        assertThat(one).isEqualTo("1");
    }
}
