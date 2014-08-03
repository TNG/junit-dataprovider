package com.tngtech.java.junit.dataprovider.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;

@RunWith(MockitoJUnitRunner.class)
public class TestFormatterTest extends BaseTest {

    @InjectMocks
    private TestFormatter underTest;

    @Test
    public void testGetNameShouldHandleSingleValueCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { 718 };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("718");
    }

    @Test
    public void testGetNameShouldHandleMultipleValuesCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { 1024, "32", 128L };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("1024, 32, 128");
    }

    @Test
    public void testGetNameShouldHandleNullSpecially() {
        // Given:
        final Object[] parameters = new Object[] { null };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("<null>");
    }

    @Test
    public void testGetNameShouldHandleNullNullCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { null, null };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("<null>, <null>");
    }

    @Test
    public void testGetNameShouldHandleEmtpyStringSpecially() {
        // Given:
        final Object[] parameters = new Object[] { "" };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("<empty string>");
    }

    @Test
    public void testGetNameShouldHandleObjectArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new Object[] { 7.5, "test" } };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("[7.5, test]");
    }

    @Test
    public void testGetNameShouldHandlePrimitiveBooleanTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new boolean[] { true, false } };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("[true, false]");
    }

    @Test
    public void testGetNameShouldHandlePrimitiveByteTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new byte[] { 12, 24 } };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("[12, 24]");
    }

    @Test
    public void testGetNameShouldHandlePrimitiveCharTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new char[] { 'a', '0' } };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("[a, 0]");
    }

    @Test
    public void testGetNameShouldHandlePrimitiveShortTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new short[] { 1024 } };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("[1024]");
    }

    @Test
    public void testGetNameShouldHandlePrimitiveIntTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new int[] { 11, 2 } };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("[11, 2]");
    }

    @Test
    public void testGetNameShouldHandlePrimitiveLongTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new long[] { 111L, 222L, 333L } };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("[111, 222, 333]");
    }

    @Test
    public void testGetNameShouldHandlePrimitiveFloatTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new float[] { 0.3f, 0.9f, 0.81f, 0.6561f } };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("[0.3, 0.9, 0.81, 0.6561]");
    }

    @Test
    public void testGetNameShouldHandlePrimitiveDoubleTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new double[] { .78, 3.15E2 } };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("[0.78, 315.0]");
    }

    @Test
    public void testGetNameShouldHandleFurtherNestedArraysCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new Object[] { 2, new char[] { 'a', 'b' }, new String[] { "a", "b" } } };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("[2, [a, b], [a, b]]");
    }

    @Test
    public void testGetNameShouldHandleObjectCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new Object() };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).matches("java.lang.Object@[0-9a-f]+");
    }

    @Test
    public void testGetNameShouldHandleListsCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { Arrays.<Object> asList("test", 1, 1723940567289346512L), 3 };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("[test, 1, 1723940567289346512], 3");
    }

    @Test
    public void testGetNameShouldHandleEnumsCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { Thread.State.RUNNABLE };

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo("RUNNABLE");
    }

    @Test
    public void testGetNameShouldHandleComplexExampleCorrectly() {
        // Given:
        Date now = new Date();
        // @formatter:off
        final Object[] parameters = new Object[] {
                now,
                Double.valueOf(3.5),
                new StringBuilder("1").append("|2").append("|3"),
                new File("src/main/java/com/tngtech"),
            };
        // @formatter:on

        // When:
        String result = underTest.format(parameters);

        // Then:
        assertThat(result).isEqualTo(now.toString() + ", 3.5, 1|2|3, src/main/java/com/tngtech");
    }
}
