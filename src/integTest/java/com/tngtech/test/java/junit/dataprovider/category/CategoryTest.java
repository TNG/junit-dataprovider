package com.tngtech.test.java.junit.dataprovider.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@Category(CategoryTwo.class)
@RunWith(DataProviderRunner.class)
public class CategoryTest {

    @Test
    public void testNone() {
        // Expect:
        assertThat("none").hasSize(4);
    }

    @Category(CategoryOne.class)
    @Test
    public void testOne() {
        // Expect:
        assertThat("one").hasSize(3);
    }

    @DataProvider
    public static Object[][] dataProvider() {
        // @formatter:off
        return new Object[][] {
                { "",    0 },
                { "1",   1 },
                { "12",  2 },
        };
        // @formatter:on
    }

    @Category(CategoryOne.class)
    @Test
    @UseDataProvider("dataProvider")
    public void test(String string, int expectedLength) {
        // Expect:
        assertThat(string).hasSize(expectedLength);
    }
}
