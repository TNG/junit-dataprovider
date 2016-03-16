package com.tngtech.java.junit.dataprovider.internal.placeholder;

import static com.tngtech.java.junit.dataprovider.internal.placeholder.ParameterPlaceholder.STRING_EMPTY;
import static com.tngtech.java.junit.dataprovider.internal.placeholder.ParameterPlaceholder.STRING_NON_PRINTABLE;
import static com.tngtech.java.junit.dataprovider.internal.placeholder.ParameterPlaceholder.STRING_NULL;
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
public class ParameterPlaceholderTest extends BaseTest {

    @InjectMocks
    private ParameterPlaceholder underTest;

    @Test
    public void testProcessShouldReplaceIndexSubscriptParameterPlaceholderUsingPositiveIndex() {
        // Given:
        final Object[] parameters = new Object[] { 'a', 1, 2l, 3.3 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[2]");

        // Then:
        assertThat(result).isEqualTo("2");
    }

    @Test
    public void testProcessShouldReplaceIndexSubscriptParameterPlaceholderUsingNegativeIndex() {
        // Given:
        final Object[] parameters = new Object[] { 'a', 1, 2l, 3.3 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[-3]");

        // Then:
        assertThat(result).isEqualTo("1");
    }

    @Test
    public void testProcessShouldReplaceWholeRangeSubscriptParameterPlaceholderUsingOnlyPositiveIndices() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[0..9]");

        // Then:
        assertThat(result).isEqualTo("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
    }

    @Test
    public void testProcessShouldReplaceWholeRangeSubscriptParameterPlaceholderUsingMixedIndices() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[0..-1]");

        // Then:
        assertThat(result).isEqualTo("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
    }

    @Test
    public void testProcessShouldReplaceWholeRangeSubscriptParameterPlaceholderUsingMixedIndicesOtherWayRound() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[-10..9]");

        // Then:
        assertThat(result).isEqualTo("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
    }

    @Test
    public void testProcessShouldReplaceWholeRangeSubscriptParameterPlaceholderUsingOnlyNegativeIndices() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[-10..-1]");

        // Then:
        assertThat(result).isEqualTo("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
    }

    @Test
    public void testProcessShouldReplaceWholeWithoutFirstRangeSubscriptParameterPlaceholder() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[1..-1]");

        // Then:
        assertThat(result).isEqualTo("1, 2, 3, 4, 5, 6, 7, 8, 9");
    }

    @Test
    public void testProcessShouldReplaceWholeWithoutLastRangeSubscriptParameterPlaceholderUsingOnlyPositiveIndices() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[0..8]");

        // Then:
        assertThat(result).isEqualTo("0, 1, 2, 3, 4, 5, 6, 7, 8");
    }

    @Test
    public void testProcessShouldReplacePartialRangeSubscriptParameterPlaceholderContainingJustOneValueUsingOnlyPositiveIndices() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[4..6]");

        // Then:
        assertThat(result).isEqualTo("4, 5, 6");
    }

    @Test
    public void testProcessShouldReplacePartialRangeSubscriptParameterPlaceholderContainingJustOneValueUsingMixedIndices() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[4..-4]");

        // Then:
        assertThat(result).isEqualTo("4, 5, 6");
    }

    @Test
    public void testProcessShouldReplacePartialRangeSubscriptParameterPlaceholderContainingJustOneValueUsingMixedIndicesOtherWayRound() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[-6..6]");

        // Then:
        assertThat(result).isEqualTo("4, 5, 6");
    }

    @Test
    public void testProcessShouldReplacePartialRangeSubscriptParameterPlaceholderContainingJustOneValueUsingOnlyNegativeIndices() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[-6..-4]");

        // Then:
        assertThat(result).isEqualTo("4, 5, 6");
    }

    @Test
    public void testProcessShouldReplaceSingleValueRangeSubscriptParameterPlaceholderUsingOnlyPositiveIndices() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[7..7]");

        // Then:
        assertThat(result).isEqualTo("7");
    }

    @Test
    public void testProcessShouldReplaceSingleValueRangeSubscriptParameterPlaceholderUsingMixedIndices() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[7..-3]");

        // Then:
        assertThat(result).isEqualTo("7");
    }

    @Test
    public void testProcessShouldReplaceSingleValueRangeSubscriptParameterPlaceholderUsingOnlyNegativeIndices() {
        // Given:
        final Object[] parameters = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        underTest.setContext(anyMethod(), 0, parameters);

        // When:
        String result = underTest.process("%p[-3..-3]");

        // Then:
        assertThat(result).isEqualTo("7");
    }

    @Test
    public void testFormatAllShouldHandleSingleValueCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { 12.45 };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("12.45");
    }

    @Test
    public void testFormatAllShouldReturnAllThreeValuesCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { "test", 1, 2L };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("test, 1, 2");
    }

    @Test
    public void testFormatAllHandleNullSpecially() {
        // Given:
        final Object[] parameters = new Object[] { null };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo(STRING_NULL);
    }

    @Test
    public void testFormatAllHandleNullNullCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { null, null };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("<null>, <null>");
    }

    @Test
    public void testFormatAllDoesNotThrowNullPointerExceptionIfParamsToStringReturningNull() {
        // Given:
        final Object[] parameters = new Object[] { new TestToString(null) };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("<null>");
    }

    @Test
    public void testFormatAllHandleEmtpyStringSpecially() {
        // Given:
        final Object[] parameters = new Object[] { "" };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo(STRING_EMPTY);
    }

    @Test
    public void testFormatAllReplacesNullTerminatorWithTheirPrintableCounterpart() {
        // Given:
        final Object[] parameters = new Object[] { "\0" };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("\\0");
    }

    @Test
    public void testFormatAllReplacesNullTerminatorWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final Object[] parameters = new Object[] { "test\0test\0" };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("test\\0test\\0");
    }

    @Test
    public void testFormatAllReplacesCarriageReturnWithTheirPrintableCounterpart() {
        // Given:
        final Object[] parameters = new Object[] { "\r" };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("\\r");
    }

    @Test
    public void testFormatAllReplacesCarriageReturnsWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final Object[] parameters = new Object[] { "test\rtest\r" };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("test\\rtest\\r");
    }

    @Test
    public void testFormatAllReplacesLineFeedWithTheirPrintableCounterpart() {
        // Given:
        final Object[] parameters = new Object[] { "\n" };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("\\n");
    }

    @Test
    public void testFormatAllReplacesLineFeedsWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final Object[] parameters = new Object[] { "1\n2\n3" };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("1\\n2\\n3");
    }

    @Test
    public void testFormatAllReplacesNonPrintableCharactersWithPredefinedPrintableCounterpart() {
        // Given:
        final Object[] parameters = new Object[] { "\u001F" };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo(STRING_NON_PRINTABLE);
    }

    @Test
    public void testFormatAllReplacesNonPrintableCharactersWithPredefinedPrintableCounterpartEvenIfWithText() {
        // Given:
        final Object[] parameters = new Object[] { "test\btest\uFFFF" };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("test" + STRING_NON_PRINTABLE + "test" + STRING_NON_PRINTABLE);
    }

    @Test
    public void testFormatAllReplacesCarriageReturnsAndLineFeedsWithTheirPrintableCounterpart() {
        // Given:
        final Object[] parameters = new Object[] { "A very\r\nlong text\nwith multiple\rdifferent newline\n\rvariations." };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("A very\\r\\nlong text\\nwith multiple\\rdifferent newline\\n\\rvariations.");
    }

    private static class TestToString {
        private final String toString;

        public TestToString(String toString) {
            this.toString = toString;
        }

        @Override
        public String toString() {
            return toString;
        }
    }

    @Test
    public void testFormatForCustomObjectReplacesCarriageReturnWithTheirPrintableCounterpart() {
        // Given:
        final TestToString parameter = new TestToString("\r");

        // When:
        String result = underTest.format(parameter);

        // Then:
        assertThat(result).isEqualTo("\\r");
    }

    @Test
    public void testFormatForCustomObjectReplacesCarriageReturnsWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final TestToString parameter = new TestToString("test\rtest\r");

        // When:
        String result = underTest.format(parameter);

        // Then:
        assertThat(result).isEqualTo("test\\rtest\\r");
    }

    @Test
    public void testFormatForCustomObjectReplacesLineFeedWithTheirPrintableCounterpart() {
        // Given:
        final TestToString parameter = new TestToString("\n");

        // When:
        String result = underTest.format(parameter);

        // Then:
        assertThat(result).isEqualTo("\\n");
    }

    @Test
    public void testFormatForCustomObjectReplacesLineFeedsWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final TestToString parameter = new TestToString("1\n2\n3");

        // When:
        String result = underTest.format(parameter);

        // Then:
        assertThat(result).isEqualTo("1\\n2\\n3");
    }

    @Test
    public void testFormatForCustomObjectReplacesCarriageReturnsAndLineFeedsWithTheirPrintableCounterpart() {
        // Given:
        final TestToString parameter = new TestToString(
                "A very\r\nlong text\nwith multiple\rdifferent newline\n\rvariations.");

        // When:
        String result = underTest.format(parameter);

        // Then:
        assertThat(result).isEqualTo("A very\\r\\nlong text\\nwith multiple\\rdifferent newline\\n\\rvariations.");
    }

    @Test
    public void testFormatAllHandleObjectArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new Object[] { 7.5, "test" } };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("[7.5, test]");
    }

    @Test
    public void testFormatAllHandlePrimitiveBooleanTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new boolean[] { true, false } };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("[true, false]");
    }

    @Test
    public void testFormatAllHandlePrimitiveByteTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new byte[] { 12, 24 } };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("[12, 24]");
    }

    @Test
    public void testFormatAllHandlePrimitiveCharTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new char[] { 'a', '0' } };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("[a, 0]");
    }

    @Test
    public void testFormatAllHandlePrimitiveShortTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new short[] { 1024 } };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("[1024]");
    }

    @Test
    public void testFormatAllHandlePrimitiveIntTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new int[] { 11, 2 } };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("[11, 2]");
    }

    @Test
    public void testFormatAllHandlePrimitiveLongTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new long[] { 111L, 222L, 333L } };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("[111, 222, 333]");
    }

    @Test
    public void testFormatAllHandlePrimitiveFloatTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new float[] { 0.3f, 0.9f, 0.81f, 0.6561f } };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("[0.3, 0.9, 0.81, 0.6561]");
    }

    @Test
    public void testFormatAllHandlePrimitiveDoubleTypeArrayCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new double[] { .78, 3.15E2 } };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("[0.78, 315.0]");
    }

    @Test
    public void testFormatAllHandleFurtherNestedArraysCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new Object[] { 2, new char[] { 'a', 'b' }, new String[] { "a", "b" } } };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("[2, [a, b], [a, b]]");
    }

    @Test
    public void testFormatAllHandleObjectCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { new Object() };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).matches("java.lang.Object@[0-9a-f]+");
    }

    @Test
    public void testFormatAllHandleListsCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { Arrays.<Object> asList("test", 1, 1723940567289346512L), 3 };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("[test, 1, 1723940567289346512], 3");
    }

    @Test
    public void testFormatAllHandleEnumsCorrectly() {
        // Given:
        final Object[] parameters = new Object[] { Thread.State.RUNNABLE };

        // When:
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo("RUNNABLE");
    }

    @Test
    public void testFormatAllHandleComplexExampleCorrectly() {
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
        String result = underTest.formatAll(parameters);

        // Then:
        assertThat(result).isEqualTo(now.toString() + ", 3.5, 1|2|3, src/main/java/com/tngtech");
    }
}
