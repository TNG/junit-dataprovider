package com.tngtech.junit.dataprovider;

import static com.tngtech.junit.dataprovider.DataProviders.$;
import static com.tngtech.junit.dataprovider.DataProviders.$$;
import static com.tngtech.junit.dataprovider.DataProviders.crossProduct;
import static com.tngtech.junit.dataprovider.DataProviders.testForEach;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DataProvidersTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test$ShouldReturnEmptyObjectArrayForNoArgs() {
        // Given:

        // When:
        Object[] result = $();

        // Then:
        assertThat(result).isEqualTo(new Object[0]);
    }

    @Test
    public void test$ShouldReturnObjectArrayWithSingleElementForOneArg() {
        // Given:

        // When:
        Object[] result = $("test");

        // Then:
        assertThat(result).isEqualTo(new Object[] { "test" });
    }

    @Test
    public void test$ShouldReturnObjectArrayWithAllElementsForMultipleArg() {
        // Given:
        long millis = System.currentTimeMillis();
        Date now = new Date();

        // When:
        Object[] result = $(millis, now, "equals");

        // Then:
        assertThat(result).containsExactly(millis, now, "equals");
    }

    @Test
    public void test$$ShouldReturnEmptyObjectArrayForNoArgs() {
        // Given:

        // When:
        Object[][] result = $$();

        // Then:
        assertThat(result).isEqualTo(new Object[0][0]);
    }

    @Test
    public void test$$ShouldReturnObjectArrayWithSingleElementForOneArg() {
        // Given:

        // When:
        Object[] result = $$(new Object[] { 10e-3 });

        // Then:
        assertThat(result).isEqualTo(new Object[][] { { 0.01 } });
    }

    @Test
    public void test$$ShouldReturnObjectArrayWithAllElementsForMultipleArg() {
        // Given:

        // When:
        Object[] result = $$(new Object[] { "test1", 1 }, new Object[] { "test2" }, new Object[] { "test3", 3 });

        // Then:
        assertThat(result).isEqualTo(new Object[][] { { "test1", 1 }, { "test2" }, { "test3", 3 } });
    }

    @Test
    public void test$$And$InCooperation() {
        // Given:

        // When:
        // @formatter:off
        Object[][] result = $$(
                $(0, 0, 0),
                $(0, 1, 1),
                $(1, 1, 2),
                $(1, 2, 3)
            );
        // @formatter:on

        // Then:
        assertThat(result).isEqualTo(new Object[][] { { 0, 0, 0 }, { 0, 1, 1 }, { 1, 1, 2 }, { 1, 2, 3 } });
    }

    @Test
    public void testTestForEachObjectArrayShouldReturnEmptyObjectArrayArrayForNoArg() {
        // Given:

        // When:
        Object[][] result = testForEach();

        // Then:
        assertThat(result).isEqualTo(new Object[0][0]);
    }

    @Test
    public void testTestForEachObjectArrayShouldReturnObjectArrayArrayWithSingleElementForOneArg() {
        // Given:

        // When:
        Object[][] result = testForEach(17.25);

        // Then:
        assertThat(result).isEqualTo(new Object[][] { { 17.25 } });
    }

    @Test
    public void testTestForEachObjectArrayShouldReturnObjectArrayArrayWithObjectArrayForEveryArgOnMultipleArgs() {
        // Given:

        // When:
        Object[][] result = testForEach('a', "aa", "aaa");

        // Then:
        assertThat(result).isEqualTo(new Object[][] { { 'a' }, { "aa" }, { "aaa" } });
    }

    @Test
    public void testTestForEachClassOfEnumShouldThrowNullPointerExceptionForNullArg() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'enumClass' must not be null");

        // When:
        testForEach((Class<TestEnum>) null);

        // Then: expect exception
    }

    @Test
    public void testTestForEachClassOfEnumShouldReturnObjectArrayArrayForEachEnumValue() {
        // Given:

        // When:
        Object[][] result = testForEach(TestEnum.class);

        // Then:
        assertThat(result).isEqualTo(new Object[][] { { TestEnum.VAL1 }, { TestEnum.VAL2 }, { TestEnum.VAL3 } });
    }

    @Test
    public void testCrossProductShouldReturnEmptyWhenLeftSideIsEmpty() {
        // Given:

        // When:
        Object[][] result = crossProduct(testForEach(1, 2, 3), testForEach());

        // Then:
        assertThat(result).isEqualTo(new Object[][] {});
    }

    @Test
    public void testCrossProductShouldReturnEmptyWhenRightSideIsEmpty() {
        // Given:

        // When:
        Object[][] result = crossProduct(testForEach(), testForEach(1, 2, 3));

        // Then:
        assertThat(result).isEqualTo(new Object[][] {});
    }

    @Test
    public void testCrossProductShouldReturnTheCrossProductOfBothSides() {
        // Given:

        // When:
        Object[][] result = crossProduct(testForEach(1, 2, 3), testForEach(4, 5));

        // Then:
        assertThat(result).isEqualTo(new Object[][] { { 1, 4 }, { 1, 5 }, { 2, 4 }, { 2, 5 }, { 3, 4 }, { 3, 5 } });
    }

    // -- test data ----------------------------------------------------------------------------------------------------

    protected static enum TestEnum {
        VAL1, VAL2, VAL3
    }
}
