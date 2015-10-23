package com.tngtech.test.java.junit.dataprovider;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderNewlineAcceptanceTest {

    private static class NewlinesInToString {
        private final int lines;

        public NewlinesInToString(int lines) {
            this.lines = lines;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < lines; i++) {
                b.append("Line " + i + "\n");
            }
            return b.toString();
        }

    }

    @DataProvider
    public static Object[][] dataProviderNewlinesWithinParameters() {
        // @formatter:off
        return new Object[][] {
            { new NewlinesInToString(0) },
            { new NewlinesInToString(1) },
            { new NewlinesInToString(2) },
            { new NewlinesInToString(3) },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderNewlinesWithinParameters")
    public void testNewlinesWithinParameters(@SuppressWarnings("unused") NewlinesInToString object) {
        // Check output within IDE
    }
}