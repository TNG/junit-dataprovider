package com.tngtech.test.junit.dataprovider;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.DataProviderExtension;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(DataProviderExtension.class)
@ExtendWith(UseDataProviderExtension.class)
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
                b.append("Line " + i + "\n");
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

    @TestTemplate
    @UseDataProvider("dataProviderNewlinesWithinParameters")
    void testNewlinesWithinParameters(@SuppressWarnings("unused") NewlinesInToString object) {
        // Check output within IDE
    }

    @TestTemplate
    @DataProvider({ "Do it.\nOr let it." })
    void testWithStringContainingTabsNewlineAndCarriageReturn(@SuppressWarnings("unused") String string) {
        // nothing to do => Just look at the test output in Eclispe's JUnit view if it is displayed correctly
    }
}