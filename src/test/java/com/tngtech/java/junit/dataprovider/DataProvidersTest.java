package com.tngtech.java.junit.dataprovider;

import static com.tngtech.java.junit.dataprovider.DataProviders.$;
import static com.tngtech.java.junit.dataprovider.DataProviders.$$;
import static com.tngtech.java.junit.dataprovider.DataProviders.testForEach;
import static com.tngtech.java.junit.dataprovider.DataProviders.crossProduct;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class DataProvidersTest extends BaseTest {

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

    @Test(expected = NullPointerException.class)
    public void testTestForEachIterableShouldThrowNullPointerExceptionForNullArg() {
        // Given:

        // When:
        testForEach((Iterable<Object>) null);

        // Then: expect exception
    }

    @Test
    public void testTestForEachIterableShouldReturnObjectArrayArrayWithObjectArrayForEverySetEntry() {
        // Given:
        Set<Float> set = new HashSet<Float>();
        set.add(1.7f);
        set.add(238.78239f);

        // When:
        Object[][] result = testForEach(set);

        // Then:
        assertThat(result).contains(new Object[] { Float.valueOf(1.7f) }, new Object[] { Float.valueOf(238.78239f) });
    }

    @Test
    public void testTestForEachIterableShouldReturnObjectArrayArrayWithObjectArrayForEveryListEntry() {
        // Given:
        List<Long> list = new ArrayList<Long>();
        list.add(261l);
        list.add(167120l);

        // When:
        Object[][] result = testForEach(list);

        // Then:
        assertThat(result).isEqualTo(new Object[][] { { Long.valueOf(261l) }, { Long.valueOf(167120l) } });
    }

    @Test(expected = NullPointerException.class)
    public void testTestForEachClassOfEnumShouldThrowNullPointerExceptionForNullArg() {
        // Given:

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
    	Object[][] result = crossProduct(testForEach(1,2,3), testForEach());

        // Then:
        assertThat(result).isEqualTo(new Object[][] { });
    }
    
    @Test
    public void testCrossProductShouldReturnEmptyWhenRightSideIsEmpty() {
        // Given:

        // When:
    	Object[][] result = crossProduct(testForEach(), testForEach(1,2,3));

        // Then:
        assertThat(result).isEqualTo(new Object[][] { });
    }
    
    @Test
    public void testCrossProductShouldReturnTheCrossProductOfBothSides() {
        // Given:

        // When:
    	Object[][] result = crossProduct(testForEach(1,2,3), testForEach(4,5));

        // Then:
        assertThat(result).isEqualTo(new Object[][] { {1, 4}, { 1, 5 }, { 2, 4 }, { 2, 5 }, { 3, 4 }, { 3, 5 } });
    }
}
