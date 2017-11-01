package com.tngtech.junit.dataprovider.placeholder;

import static com.tngtech.junit.dataprovider.placeholder.ArgumentPlaceholder.STRING_EMPTY;
import static com.tngtech.junit.dataprovider.placeholder.ArgumentPlaceholder.STRING_NON_PRINTABLE;
import static com.tngtech.junit.dataprovider.placeholder.ArgumentPlaceholder.STRING_NULL;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.tngtech.junit.dataprovider.testutils.Methods;

public class ArgumentPlaceholderTest {

    private final ArgumentPlaceholder underTest = new ArgumentPlaceholder();

    @Test
    public void testProcessShouldReplaceIndexSubscriptArgumentPlaceholderUsingPositiveIndex() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%p[2]");

        // Then:
        assertThat(result).isEqualTo("2");
    }

    @Test
    public void testProcessShouldReplaceIndexSubscriptArgumentPlaceholderUsingNegativeIndex() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[-3]");

        // Then:
        assertThat(result).isEqualTo("1");
    }

    @Test
    public void testProcessShouldReplaceWholeRangeSubscriptArgumentPlaceholderUsingOnlyPositiveIndices() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%p[0..9]");

        // Then:
        assertThat(result).isEqualTo("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
    }

    @Test
    public void testProcessShouldReplaceWholeRangeSubscriptArgumentPlaceholderUsingMixedIndices() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[0..-1]");

        // Then:
        assertThat(result).isEqualTo("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
    }

    @Test
    public void testProcessShouldReplaceWholeRangeSubscriptArgumentPlaceholderUsingMixedIndicesOtherWayRound() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[-10..9]");

        // Then:
        assertThat(result).isEqualTo("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
    }

    @Test
    public void testProcessShouldReplaceWholeRangeSubscriptArgumentPlaceholderUsingOnlyNegativeIndices() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[-10..-1]");

        // Then:
        assertThat(result).isEqualTo("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
    }

    @Test
    public void testProcessShouldReplaceWholeWithoutFirstRangeSubscriptArgumentPlaceholder() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[1..-1]");

        // Then:
        assertThat(result).isEqualTo("1, 2, 3, 4, 5, 6, 7, 8, 9");
    }

    @Test
    public void testProcessShouldReplaceWholeWithoutLastRangeSubscriptArgumentPlaceholderUsingOnlyPositiveIndices() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[0..8]");

        // Then:
        assertThat(result).isEqualTo("0, 1, 2, 3, 4, 5, 6, 7, 8");
    }

    @Test
    public void testProcessShouldReplacePartialRangeSubscriptArgumentPlaceholderContainingJustOneValueUsingOnlyPositiveIndices() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[4..6]");

        // Then:
        assertThat(result).isEqualTo("4, 5, 6");
    }

    @Test
    public void testProcessShouldReplacePartialRangeSubscriptArgumentPlaceholderContainingJustOneValueUsingMixedIndices() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[4..-4]");

        // Then:
        assertThat(result).isEqualTo("4, 5, 6");
    }

    @Test
    public void testProcessShouldReplacePartialRangeSubscriptArgumentPlaceholderContainingJustOneValueUsingMixedIndicesOtherWayRound() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[-6..6]");

        // Then:
        assertThat(result).isEqualTo("4, 5, 6");
    }

    @Test
    public void testProcessShouldReplacePartialRangeSubscriptArgumentPlaceholderContainingJustOneValueUsingOnlyNegativeIndices() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[-6..-4]");

        // Then:
        assertThat(result).isEqualTo("4, 5, 6");
    }

    @Test
    public void testProcessShouldReplaceSingleValueRangeSubscriptArgumentPlaceholderUsingOnlyPositiveIndices() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[7..7]");

        // Then:
        assertThat(result).isEqualTo("7");
    }

    @Test
    public void testProcessShouldReplaceSingleValueRangeSubscriptArgumentPlaceholderUsingMixedIndices() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[7..-3]");

        // Then:
        assertThat(result).isEqualTo("7");
    }

    @Test
    public void testProcessShouldReplaceSingleValueRangeSubscriptArgumentPlaceholderUsingOnlyNegativeIndices() {
        // Given:
        final List<Object> arguments = list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), 0, arguments);

        // When:
        String result = underTest.process(data, "%a[-3..-3]");

        // Then:
        assertThat(result).isEqualTo("7");
    }

    @Test
    public void testFormatAllShouldHandleSingleValueCorrectly() {
        // Given:
        final List<Object> arguments = list(12.45);

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("12.45");
    }

    @Test
    public void testFormatAllShouldReturnAllThreeValuesCorrectly() {
        // Given:
        final List<Object> arguments = list("test", 1, 2L);

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("test, 1, 2");
    }

    @Test
    public void testFormatAllHandleNullSpecially() {
        // Given:
        final List<Object> arguments = list(null);

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo(STRING_NULL);
    }

    @Test
    public void testFormatAllHandleNullNullCorrectly() {
        // Given:
        final List<Object> arguments = list(null, (Object) null); // cast to suppress compiler warning

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("<null>, <null>");
    }

    @Test
    public void testFormatAllDoesNotThrowNullPointerExceptionIfParamsToStringReturningNull() {
        // Given:
        final List<Object> arguments = list(new TestToString(null));

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("<null>");
    }

    @Test
    public void testFormatAllHandleEmtpyStringSpecially() {
        // Given:
        final List<Object> arguments = list("");

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo(STRING_EMPTY);
    }

    @Test
    public void testFormatAllReplacesNullTerminatorWithTheirPrintableCounterpart() {
        // Given:
        final List<Object> arguments = list("\0");

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("\\0");
    }

    @Test
    public void testFormatAllReplacesNullTerminatorWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final List<Object> arguments = list("test\0test\0");

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("test\\0test\\0");
    }

    @Test
    public void testFormatAllReplacesCarriageReturnWithTheirPrintableCounterpart() {
        // Given:
        final List<Object> arguments = list("\r");

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("\\r");
    }

    @Test
    public void testFormatAllReplacesCarriageReturnsWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final List<Object> arguments = list("test\rtest\r");

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("test\\rtest\\r");
    }

    @Test
    public void testFormatAllReplacesLineFeedWithTheirPrintableCounterpart() {
        // Given:
        final List<Object> arguments = list("\n");

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("\\n");
    }

    @Test
    public void testFormatAllReplacesLineFeedsWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final List<Object> arguments = list("1\n2\n3");

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("1\\n2\\n3");
    }

    @Test
    public void testFormatAllReplacesNonPrintableCharactersWithPredefinedPrintableCounterpart() {
        // Given:
        final List<Object> arguments = list("\u001F");

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo(STRING_NON_PRINTABLE);
    }

    @Test
    public void testFormatAllReplacesNonPrintableCharactersWithPredefinedPrintableCounterpartEvenIfWithText() {
        // Given:
        final List<Object> arguments = list("test\btest\uFFFF");

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("test" + STRING_NON_PRINTABLE + "test" + STRING_NON_PRINTABLE);
    }

    @Test
    public void testFormatAllReplacesCarriageReturnsAndLineFeedsWithTheirPrintableCounterpart() {
        // Given:
        final List<Object> arguments = list("A very\r\nlong text\nwith multiple\rdifferent newline\n\rvariations.");

        // When:
        String result = underTest.formatAll(arguments);

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
        final TestToString argument = new TestToString("\r");

        // When:
        String result = underTest.format(argument);

        // Then:
        assertThat(result).isEqualTo("\\r");
    }

    @Test
    public void testFormatForCustomObjectReplacesCarriageReturnsWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final TestToString argument = new TestToString("test\rtest\r");

        // When:
        String result = underTest.format(argument);

        // Then:
        assertThat(result).isEqualTo("test\\rtest\\r");
    }

    @Test
    public void testFormatForCustomObjectReplacesLineFeedWithTheirPrintableCounterpart() {
        // Given:
        final TestToString argument = new TestToString("\n");

        // When:
        String result = underTest.format(argument);

        // Then:
        assertThat(result).isEqualTo("\\n");
    }

    @Test
    public void testFormatForCustomObjectReplacesLineFeedsWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final TestToString argument = new TestToString("1\n2\n3");

        // When:
        String result = underTest.format(argument);

        // Then:
        assertThat(result).isEqualTo("1\\n2\\n3");
    }

    @Test
    public void testFormatForCustomObjectReplacesCarriageReturnsAndLineFeedsWithTheirPrintableCounterpart() {
        // Given:
        final TestToString argument = new TestToString(
                "A very\r\nlong text\nwith multiple\rdifferent newline\n\rvariations.");

        // When:
        String result = underTest.format(argument);

        // Then:
        assertThat(result).isEqualTo("A very\\r\\nlong text\\nwith multiple\\rdifferent newline\\n\\rvariations.");
    }

    @Test
    public void testFormatForCustomObjectReplacesNullFromToString() {
        // Given:
        final TestToString argument = new TestToString(null);

        // When:
        String result = underTest.format(argument);

        // Then:
        assertThat(result).isEqualTo(STRING_NULL);
    }

    @Test
    public void testFormatAllHandleObjectArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new Object[] { 7.5, "test" });

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("[7.5, test]");
    }

    @Test
    public void testFormatAllHandlePrimitiveBooleanTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new boolean[] { true, false });

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("[true, false]");
    }

    @Test
    public void testFormatAllHandlePrimitiveByteTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new byte[] { 12, 24 });

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("[12, 24]");
    }

    @Test
    public void testFormatAllHandlePrimitiveCharTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new char[] { 'a', '0' });

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("[a, 0]");
    }

    @Test
    public void testFormatAllHandlePrimitiveShortTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new short[] { 1024 });

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("[1024]");
    }

    @Test
    public void testFormatAllHandlePrimitiveIntTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new int[] { 11, 2 });

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("[11, 2]");
    }

    @Test
    public void testFormatAllHandlePrimitiveLongTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new long[] { 111L, 222L, 333L });

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("[111, 222, 333]");
    }

    @Test
    public void testFormatAllHandlePrimitiveFloatTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new float[] { 0.3f, 0.9f, 0.81f, 0.6561f });

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("[0.3, 0.9, 0.81, 0.6561]");
    }

    @Test
    public void testFormatAllHandlePrimitiveDoubleTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new double[] { .78, 3.15E2 });

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("[0.78, 315.0]");
    }

    @Test
    public void testFormatAllHandleFurtherNestedArraysCorrectly() {
        // Given:
        final List<Object> arguments = list(new Object[] { 2, new char[] { 'a', 'b' }, new String[] { "a", "b" } });

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("[2, [a, b], [a, b]]");
    }

    @Test
    public void testFormatAllHandleObjectCorrectly() {
        // Given:
        final List<Object> arguments = list(new Object());

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).matches("java.lang.Object@[0-9a-f]+");
    }

    @Test
    public void testFormatAllHandleListsCorrectly() {
        // Given:
        final List<Object> arguments = list(list("test", 1, 1723940567289346512L), 3);

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("[test, 1, 1723940567289346512], 3");
    }

    @Test
    public void testFormatAllHandleEnumsCorrectly() {
        // Given:
        final List<Object> arguments = list(Thread.State.RUNNABLE);

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo("RUNNABLE");
    }

    @Test
    public void testFormatAllHandleComplexExampleCorrectly() {
        // Given:
        Date now = new Date();
        // @formatter:off
        final List<Object> arguments = list(
                now,
                Double.valueOf(3.5),
                new StringBuilder("1").append("|2").append("|3"),
                new File("src/main/java/com/tngtech")
            );
        // @formatter:on

        // When:
        String result = underTest.formatAll(arguments);

        // Then:
        assertThat(result).isEqualTo(now.toString() + ", 3.5, 1|2|3, src/main/java/com/tngtech");
    }

    // -- helper methods -----------------------------------------------------------------------------------------------

    private List<Object> list(Object first, Object... remaining) {
        List<Object> result = new ArrayList<Object>();
        result.add(first);
        for (Object object : remaining) {
            result.add(object);
        }
        return result;
    }
}
