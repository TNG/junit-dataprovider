package com.tngtech.test.java.junit.dataprovider;

import static org.fest.assertions.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderEclipseJUnitPluginUnrootedReproducerAcceptanceTest {

    @DataProvider
    public static Object[][] dataProvider() {

        final Object obj = new Object();

        // @formatter:off
        return new Object[][] {
                { DateTime.now() },
                { DateTime.now().toString() },
                { DateTime.now().toString("yyyy-MM-dd HH:mm:ss.SSSZZ") },
                { DateTime.now().toString("yyyy-MM-dd HH:mm:ss") },
                { DateTime.now().toString("HH:mm:ss.SSS") },
                { DateTime.now().toString("ss.SSS") },
                { new DateTime(2013, 4, 12, 11, 20, 22, 576, DateTimeZone.forOffsetHours(2)) },
                { DateTime.parse("2013-04-12T11:20:22.576+02:00") },
                { DateTime.parse("2013-04-12T11:20:22.576+02:00").toString() },
                { "2013-04-12T11:20:22.576+02:00" },

                { obj },
                { obj.toString() },
                { obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode()) },
                { Integer.toHexString(obj.hashCode()) },
                { Integer.toHexString(504813386) },
                { "43c57a6a" },
                { "Test\nTest\tTest" },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProvider")
    public void test(Object obj) {

        // Given:

        // When:

        // Then:
        assertThat(obj).isNotNull();
    }
}
