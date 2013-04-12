package com.tngtech.test.java.junit.dataprovider;

import static org.fest.assertions.api.Assertions.assertThat;

import org.joda.time.DateTime;
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
//                { DateTime.parse("2013-04-12T11:20:22.576+02:00") },
//                { DateTime.parse("2013-04-12T11:20:22.576+02:00").toString() },
//                { "2013-04-12T11:20:22.576+02:00" },

                { obj },
                { obj.toString() },
//                { obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode()) },
                { Integer.toHexString(obj.hashCode()) },
//                { Integer.toHexString(1300681493) },
//                { "4dfbca86" },
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
