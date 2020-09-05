package com.tngtech.junit.dataprovider;

import static com.tngtech.junit.dataprovider.DataProviders.$;
import static com.tngtech.junit.dataprovider.DataProviders.$$;
import static com.tngtech.junit.dataprovider.DataProviders.crossProduct;
import static com.tngtech.junit.dataprovider.DataProviders.crossProductSingleArg;
import static com.tngtech.junit.dataprovider.DataProviders.testForEach;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DataProvidersTest {

    @SuppressWarnings("deprecation")
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
    public void testCrossProductObjectArrayArrayShouldReturnEmptyWhenLeftSideIsEmpty() {
        // Given:

        // When:
        Object[][] result = crossProduct(testForEach(1, 2, 3), testForEach());

        // Then:
        assertThat(result).isEqualTo(new Object[][] {});
    }

    @Test
    public void testCrossProductObjectArrayArrayShouldReturnEmptyWhenRightSideIsEmpty() {
        // Given:

        // When:
        Object[][] result = crossProduct(testForEach(), testForEach(1, 2, 3));

        // Then:
        assertThat(result).isEqualTo(new Object[][] {});
    }

    @Test
    public void testCrossProductObjectArrayArrayShouldReturnTheCrossProductOfBothSidesForOneElementArrays() {
        // Given:

        // When:
        Object[][] result = crossProduct(testForEach(1, 2, 3), testForEach(4, 5));

        // Then:
        assertThat(result).isEqualTo(new Object[][] { { 1, 4 }, { 1, 5 }, { 2, 4 }, { 2, 5 }, { 3, 4 }, { 3, 5 } });
    }

    @Test
    public void testCrossProductObjectArrayArrayShouldReturnTheCrossProductOfBothSidesForMoreComplexArrays() {
        // Given:

        // When:
        Object[][] result = crossProduct(new Object[][] { { 1, 2 }, { 3, 4 } },
                new Object[][] { { 5, 6, 7 }, { 8, 9, 0 } });

        // Then:
        assertThat(result).isEqualTo(
                new Object[][] { { 1, 2, 5, 6, 7 }, { 1, 2, 8, 9, 0 }, { 3, 4, 5, 6, 7 }, { 3, 4, 8, 9, 0 } });
    }

    @Test
    public void testCrossProductSingleArgObjectArrayShouldReturnEmptyWhenLeftSideIsEmpty() {
        // Given:

        // When:
        Object[][] result = crossProductSingleArg(new Object[] { 1, 2, 3 }, new Object[0]);

        // Then:
        assertThat(result).isEqualTo(new Object[][] {});
    }

    @Test
    public void testCrossProductSingleArgObjectArrayShouldReturnEmptyWhenRightSideIsEmpty() {
        // Given:

        // When:
        Object[][] result = crossProductSingleArg(new Object[0], new Object[] { 1, 2, 3 });

        // Then:
        assertThat(result).isEqualTo(new Object[][] {});
    }

    @Test
    public void testCrossProductSingleArgObjectArrayShouldReturnTheCrossProductOfBothSides() {
        // Given:

        // When:
        Object[][] result = crossProductSingleArg(new Object[] { 1, 2, 3 }, new Object[] { 4, 5 });

        // Then:
        assertThat(result).isEqualTo(new Object[][] { { 1, 4 }, { 1, 5 }, { 2, 4 }, { 2, 5 }, { 3, 4 }, { 3, 5 } });
    }

    @Test
    public void testCrossProductIterableShouldReturnEmptyWhenLeftSideIsEmpty() {
        // Given:

        // When:
        @SuppressWarnings("unchecked")
        Object[][] result = crossProduct(asList(singletonList(1), singletonList(2), singletonList(3)),
                Collections.<Iterable<Object>>emptyList());

        // Then:
        assertThat(result).isEqualTo(new Object[][] {});
    }

    @Test
    public void testCrossProductIterableShouldReturnEmptyWhenRightSideIsEmpty() {
        // Given:

        // When:
        @SuppressWarnings("unchecked")
        Object[][] result = crossProduct(Collections.<Iterable<Object>>emptyList(),
                asList(singletonList(1), singletonList(2), singletonList(3)));

        // Then:
        assertThat(result).isEqualTo(new Object[][] {});
    }

    @Test
    public void testCrossProductIterableShouldReturnTheCrossProductOfBothSidesForOneElementIterables() {
        // Given:

        // When:
        @SuppressWarnings("unchecked")
        Object[][] result = crossProduct(
                asList(singletonList(1), singletonList(2), singletonList(3)),
                asList(singletonList(4), singletonList(5)));

        // Then:
        assertThat(result).isEqualTo(new Object[][] { { 1, 4 }, { 1, 5 }, { 2, 4 }, { 2, 5 }, { 3, 4 }, { 3, 5 } });
    }

    @Test
    public void testCrossProductIterableShouldReturnTheCrossProductOfBothSidesForMoreComplexIterables() {
        // Given:

        // When:
        @SuppressWarnings("unchecked")
        Object[][] result = crossProduct(asList(asList(1, 2), asList(3, 4)), asList(asList(5, 6, 7), asList(8, 9, 0)));

        // Then:
        assertThat(result).isEqualTo(
                new Object[][] { { 1, 2, 5, 6, 7 }, { 1, 2, 8, 9, 0 }, { 3, 4, 5, 6, 7 }, { 3, 4, 8, 9, 0 } });
    }

    @Test
    public void testCrossProductSingleArgIterableShouldReturnEmptyWhenLeftSideIsEmpty() {
        // Given:

        // When:
        Object[][] result = crossProductSingleArg(asList(1, 2, 3), emptyList());

        // Then:
        assertThat(result).isEqualTo(new Object[][] {});
    }

    @Test
    public void testCrossProductSingleArgIterableShouldReturnEmptyWhenRightSideIsEmpty() {
        // Given:

        // When:
        Object[][] result = crossProductSingleArg(emptyList(), asList(1, 2, 3));

        // Then:
        assertThat(result).isEqualTo(new Object[][] {});
    }

    @Test
    public void testCrossProductSingleArgIterableShouldReturnTheCrossProductOfBothSides() {
        // Given:

        // When:
        @SuppressWarnings("unchecked")
        Object[][] result = crossProductSingleArg(asList(1, "2", 3l), asList(4, 5.0));

        // Then:
        assertThat(result)
                .isEqualTo(new Object[][] { { 1, 4 }, { 1, 5.0 }, { "2", 4 }, { "2", 5.0 }, { 3l, 4 }, { 3l, 5.0 } });
    }

    // -- Test data ----------------------------------------------------------------------------------------------------

    protected enum TestEnum {
        VAL1, VAL2, VAL3
    }
}
