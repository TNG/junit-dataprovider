package com.tngtech.test.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Preconditions.checkNotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.UseDataProviderExtension;

@ExtendWith(UseDataProviderExtension.class)
class DataproviderMethodParameterAcceptanceTest {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface ExternalFile {
        enum Format {
            CSV,
            XML,
            XLS;
        }

        Format format();

        String value();
    }

    @DataProvider
    static Object[][] loadFromExternalFile(TestInfo testInfo, TestReporter testReporter) {
        checkNotNull(testInfo, "'testInfo' is not set");
        checkNotNull(testReporter, "'testReporter' is not set");

        String testDataFile = testInfo.getTestMethod().get().getAnnotation(ExternalFile.class).value();
        // Load the data from the external file here ...
        return new Object[][] { { testDataFile } };
    }

    @TestTemplate
    @UseDataProvider("loadFromExternalFile")
    @ExternalFile(format = ExternalFile.Format.CSV, value = "testdata.csv")
    void testThatUsesUniversalDataProvider(String testData) {
        // Expect:
        assertThat(testData).isEqualTo("testdata.csv");
    }
}
