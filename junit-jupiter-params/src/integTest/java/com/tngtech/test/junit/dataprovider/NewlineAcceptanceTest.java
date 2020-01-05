package com.tngtech.test.junit.dataprovider;

import org.junit.jupiter.params.ParameterizedTest;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;

class NewlineAcceptanceTest {

    private static class NewlinesInToString {
        private final int lines;

        NewlinesInToString(int lines) {
            this.lines = lines;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < lines; i++) {
                b.append("Line ").append(i).append("\n");
            }
            return b.toString();
        }

    }

    @DataProvider
    static Object[][] dataProviderNewlinesWithinParameters() {
        // @formatter:off
        return new Object[][] {
            { new NewlinesInToString(0) },
            { new NewlinesInToString(1) },
            { new NewlinesInToString(2) },
            { new NewlinesInToString(3) },
        };
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider("dataProviderNewlinesWithinParameters")
    void testNewlinesWithinParameters(@SuppressWarnings("unused") NewlinesInToString object) {
        // Check output within IDE
    }

    @ParameterizedTest
    @DataProvider({ "Do it.\nOr let it." })
    void testWithStringContainingTabsNewlineAndCarriageReturn(@SuppressWarnings("unused") String string) {
        // nothing to do => Just look at the test output in Eclispe's JUnit view if it is displayed correctly
    }
}