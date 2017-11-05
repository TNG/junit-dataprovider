package com.tngtech.junit.dataprovider.placeholder;

import static com.tngtech.junit.dataprovider.placeholder.NamedArgumentPlaceholder.STRING_EMPTY;
import static com.tngtech.junit.dataprovider.placeholder.NamedArgumentPlaceholder.STRING_NON_PRINTABLE;
import static com.tngtech.junit.dataprovider.placeholder.NamedArgumentPlaceholder.STRING_NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.ReflectionSupport;

class NamedArgumentPlaceholderTest {

    private static OutputStream logCapturingStream;
    private static StreamHandler customLogHandler;

    private final NamedArgumentPlaceholder underTest = new NamedArgumentPlaceholder();

    private Method tenParamMethod;

    @BeforeEach
    void prepareLogCapturing() {
        // Logger matches Logger in ClassUnderTest
        Logger logger = Logger.getLogger(NamedArgumentPlaceholder.class.getName());

        logCapturingStream = new ByteArrayOutputStream();
        Handler[] handlers = logger.getParent().getHandlers();
        customLogHandler = new StreamHandler(logCapturingStream, handlers[0].getFormatter());

        logger.addHandler(customLogHandler);
    }

    @BeforeEach
    void setup() {
        tenParamMethod = ReflectionSupport
                .findMethod(getClass(), "tenParamMethod",
                        new Class<?>[] { char.class, int.class, long.class, double.class, String.class, char.class,
                                int.class, long.class, double.class, float.class })
                .orElseThrow(
                        () -> new IllegalStateException("Could not find method having ten parameters for testing."));
    }

    @Test
    void testProcessShouldReplaceIndexSubscriptArgumentPlaceholderUsingPositiveIndex() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[2]");

        // Then:
        assertThat(result).isEqualTo("l2=2");
    }

    @Test
    void testProcessShouldReplaceIndexSubscriptArgumentPlaceholderUsingNegativeIndex() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[-3]");

        // Then:
        assertThat(result).isEqualTo("l7=7");
    }

    @Test
    void testProcessShouldReplaceWholeRangeSubscriptArgumentPlaceholderUsingOnlyPositiveIndices() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[0..9]");

        // Then:
        assertThat(result).isEqualTo("c0=a, i1=1, l2=2, d3=3.3, s4=four, c5=f, i6=6, l7=7, d8=8.8, f9=9.99");
    }

    @Test
    void testProcessShouldReplaceWholeRangeSubscriptArgumentPlaceholderUsingMixedIndices() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[0..-1]");

        // Then:
        assertThat(result).isEqualTo("c0=a, i1=1, l2=2, d3=3.3, s4=four, c5=f, i6=6, l7=7, d8=8.8, f9=9.99");
    }

    @Test
    void testProcessShouldReplaceWholeRangeSubscriptArgumentPlaceholderUsingMixedIndicesOtherWayRound() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[-10..9]");

        // Then:
        assertThat(result).isEqualTo("c0=a, i1=1, l2=2, d3=3.3, s4=four, c5=f, i6=6, l7=7, d8=8.8, f9=9.99");
    }

    @Test
    void testProcessShouldReplaceWholeRangeSubscriptArgumentPlaceholderUsingOnlyNegativeIndices() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[-10..-1]");

        // Then:
        assertThat(result).isEqualTo("c0=a, i1=1, l2=2, d3=3.3, s4=four, c5=f, i6=6, l7=7, d8=8.8, f9=9.99");
    }

    @Test
    void testProcessShouldReplaceWholeWithoutFirstRangeSubscriptArgumentPlaceholder() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[1..-1]");

        // Then:
        assertThat(result).isEqualTo("i1=1, l2=2, d3=3.3, s4=four, c5=f, i6=6, l7=7, d8=8.8, f9=9.99");
    }

    @Test
    void testProcessShouldReplaceWholeWithoutLastRangeSubscriptArgumentPlaceholderUsingOnlyPositiveIndices() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[0..8]");

        // Then:
        assertThat(result).isEqualTo("c0=a, i1=1, l2=2, d3=3.3, s4=four, c5=f, i6=6, l7=7, d8=8.8");
    }

    @Test
    void testProcessShouldReplacePartialRangeSubscriptArgumentPlaceholderContainingJustOneValueUsingOnlyPositiveIndices() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[4..6]");

        // Then:
        assertThat(result).isEqualTo("s4=four, c5=f, i6=6");
    }

    @Test
    void testProcessShouldReplacePartialRangeSubscriptArgumentPlaceholderContainingJustOneValueUsingMixedIndices() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[4..-4]");

        // Then:
        assertThat(result).isEqualTo("s4=four, c5=f, i6=6");
    }

    @Test
    void testProcessShouldReplacePartialRangeSubscriptArgumentPlaceholderContainingJustOneValueUsingMixedIndicesOtherWayRound() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[-6..6]");

        // Then:
        assertThat(result).isEqualTo("s4=four, c5=f, i6=6");
    }

    @Test
    void testProcessShouldReplacePartialRangeSubscriptArgumentPlaceholderContainingJustOneValueUsingOnlyNegativeIndices() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[-6..-4]");

        // Then:
        assertThat(result).isEqualTo("s4=four, c5=f, i6=6");
    }

    @Test
    void testProcessShouldReplaceSingleValueRangeSubscriptArgumentPlaceholderUsingOnlyPositiveIndices() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[7..7]");

        // Then:
        assertThat(result).isEqualTo("l7=7");
    }

    @Test
    void testProcessShouldReplaceSingleValueRangeSubscriptArgumentPlaceholderUsingMixedIndices() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[7..-3]");

        // Then:
        assertThat(result).isEqualTo("l7=7");
    }

    @Test
    void testProcessShouldReplaceSingleValueRangeSubscriptArgumentPlaceholderUsingOnlyNegativeIndices() {
        // Given:
        final List<Object> arguments = list('a', 1, 2l, 3.3, "four", 'f', 6, 7l, 8.8, 9.99f);

        ReplacementData data = ReplacementData.of(tenParamMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[-3..-3]");

        // Then:
        assertThat(result).isEqualTo("l7=7");
    }

    @Test
    void testProcessToDoShouldReplaceSingleValueRangeSubscriptArgumentPlaceholderUsingOnlyNegativeIndices() {
        // Given:
        final Method testMethod = ReflectionSupport
                .findMethod(Assertions.class, "assertTrue", new Class<?>[] { boolean.class })
                .orElseThrow(() -> new IllegalStateException("Could not find method"));

        final List<Object> arguments = list('x');


        ReplacementData data = ReplacementData.of(testMethod, 0, arguments);

        // When:
        String result = underTest.process(data, "%na[0]");

        // Then:
        assertThat(result).isEqualTo("arg0=x");
        assertThat(getTestCapturedLog()).containsPattern(
                "WARNING: Parameter names on method '.*' are not available. To store formal parameter names, compile the source file with the '-parameters' option");
    }

    @Test
    void testFormatAllShouldHandleSingleValueCorrectly() {
        // Given:
        final List<Object> arguments = list(12.45);

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=12.45");
    }

    @Test
    void testFormatAllShouldReturnAllThreeValuesCorrectly() {
        // Given:
        final List<Object> arguments = list("test", 1, 2L);

        // When:
        String result = underTest.formatAll(paramsWith(3), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=test, i1=1, l2=2");
    }

    @Test
    void testFormatAllHandleNullSpecially() {
        // Given:
        final List<Object> arguments = list(null);

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=" + STRING_NULL);
    }

    @Test
    void testFormatAllHandleNullNullCorrectly() {
        // Given:
        final List<Object> arguments = list(null, (Object) null); // cast to suppress compiler warning

        // When:
        String result = underTest.formatAll(paramsWith(2), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=<null>, i1=<null>");
    }

    @Test
    void testFormatAllDoesNotThrowNullPointerExceptionIfParamsToStringReturningNull() {
        // Given:
        final List<Object> arguments = list(new TestToString(null));

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=" + STRING_NULL);
    }

    @Test
    void testFormatAllHandleEmtpyStringSpecially() {
        // Given:
        final List<Object> arguments = list("");

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=" + STRING_EMPTY);
    }

    @Test
    void testFormatAllReplacesNullTerminatorWithTheirPrintableCounterpart() {
        // Given:
        final List<Object> arguments = list("\0");

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=\\0");
    }

    @Test
    void testFormatAllReplacesNullTerminatorWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final List<Object> arguments = list("test\0test\0");

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=test\\0test\\0");
    }

    @Test
    void testFormatAllReplacesCarriageReturnWithTheirPrintableCounterpart() {
        // Given:
        final List<Object> arguments = list("\r");

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=\\r");
    }

    @Test
    void testFormatAllReplacesCarriageReturnsWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final List<Object> arguments = list("test\rtest\r");

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=test\\rtest\\r");
    }

    @Test
    void testFormatAllReplacesLineFeedWithTheirPrintableCounterpart() {
        // Given:
        final List<Object> arguments = list("\n");

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=\\n");
    }

    @Test
    void testFormatAllReplacesLineFeedsWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final List<Object> arguments = list("1\n2\n3");

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=1\\n2\\n3");
    }

    @Test
    void testFormatAllReplacesNonPrintableCharactersWithPredefinedPrintableCounterpart() {
        // Given:
        final List<Object> arguments = list("\u001F");

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=" + STRING_NON_PRINTABLE);
    }

    @Test
    void testFormatAllReplacesNonPrintableCharactersWithPredefinedPrintableCounterpartEvenIfWithText() {
        // Given:
        final List<Object> arguments = list("test\btest\uFFFF");

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=test" + STRING_NON_PRINTABLE + "test" + STRING_NON_PRINTABLE);
    }

    @Test
    void testFormatAllReplacesCarriageReturnsAndLineFeedsWithTheirPrintableCounterpart() {
        // Given:
        final List<Object> arguments = list("A very\r\nlong text\nwith multiple\rdifferent newline\n\rvariations.");

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=A very\\r\\nlong text\\nwith multiple\\rdifferent newline\\n\\rvariations.");
    }

    @Test
    void testFormatAllShouldPrintQuestionMarkForUnknownParametersToBeFailureTolerantInsteadOfThrowAnException() {
        // Given:
        final List<Object> arguments = list(0, '1', 2.0);

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=0, ?=1, ?=2.0");
    }

    @Test
    void testFormatAllShouldOnlyPrintArgumentsEvenIfTooManyParametersAreGiven() {
        // Given:
        final List<Object> arguments = list(0, '1', 2.0, 3l);

        // When:
        String result = underTest.formatAll(paramsWith(10), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=0, i1=1, l2=2.0, d3=3");
    }

    @Test
    void testFormatForCustomObjectReplacesCarriageReturnWithTheirPrintableCounterpart() {
        // Given:
        final TestToString argument = new TestToString("\r");

        // When:
        String result = underTest.format(argument);

        // Then:
        assertThat(result).isEqualTo("\\r");
    }

    @Test
    void testFormatForCustomObjectReplacesCarriageReturnsWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final TestToString argument = new TestToString("test\rtest\r");

        // When:
        String result = underTest.format(argument);

        // Then:
        assertThat(result).isEqualTo("test\\rtest\\r");
    }

    @Test
    void testFormatForCustomObjectReplacesLineFeedWithTheirPrintableCounterpart() {
        // Given:
        final TestToString argument = new TestToString("\n");

        // When:
        String result = underTest.format(argument);

        // Then:
        assertThat(result).isEqualTo("\\n");
    }

    @Test
    void testFormatForCustomObjectReplacesLineFeedsWithTheirPrintableCounterpartEvenIfWithText() {
        // Given:
        final TestToString argument = new TestToString("1\n2\n3");

        // When:
        String result = underTest.format(argument);

        // Then:
        assertThat(result).isEqualTo("1\\n2\\n3");
    }

    @Test
    void testFormatForCustomObjectReplacesCarriageReturnsAndLineFeedsWithTheirPrintableCounterpart() {
        // Given:
        final TestToString argument = new TestToString(
                "A very\r\nlong text\nwith multiple\rdifferent newline\n\rvariations.");

        // When:
        String result = underTest.format(argument);

        // Then:
        assertThat(result).isEqualTo("A very\\r\\nlong text\\nwith multiple\\rdifferent newline\\n\\rvariations.");
    }

    @Test
    void testFormatForCustomObjectReplacesNullFromToString() {
        // Given:
        final TestToString argument = new TestToString(null);

        // When:
        String result = underTest.format(argument);

        // Then:
        assertThat(result).isEqualTo(STRING_NULL);
    }

    @Test
    void testFormatAllHandleObjectArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new Object[] { 7.5, "test" });

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=[7.5, test]");
    }

    @Test
    void testFormatAllHandlePrimitiveBooleanTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new boolean[] { true, false });

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=[true, false]");
    }

    @Test
    void testFormatAllHandlePrimitiveByteTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new byte[] { 12, 24 });

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=[12, 24]");
    }

    @Test
    void testFormatAllHandlePrimitiveCharTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new char[] { 'a', '0' });

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=[a, 0]");
    }

    @Test
    void testFormatAllHandlePrimitiveShortTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new short[] { 1024 });

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=[1024]");
    }

    @Test
    void testFormatAllHandlePrimitiveIntTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new int[] { 11, 2 });

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=[11, 2]");
    }

    @Test
    void testFormatAllHandlePrimitiveLongTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new long[] { 111L, 222L, 333L });

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=[111, 222, 333]");
    }

    @Test
    void testFormatAllHandlePrimitiveFloatTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new float[] { 0.3f, 0.9f, 0.81f, 0.6561f });

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=[0.3, 0.9, 0.81, 0.6561]");
    }

    @Test
    void testFormatAllHandlePrimitiveDoubleTypeArrayCorrectly() {
        // Given:
        final List<Object> arguments = list(new double[] { .78, 3.15E2 });

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=[0.78, 315.0]");
    }

    @Test
    void testFormatAllHandleFurtherNestedArraysCorrectly() {
        // Given:
        final List<Object> arguments = list(new Object[] { 2, new char[] { 'a', 'b' }, new String[] { "a", "b" } });

        // When:
        String result = underTest.formatAll(paramsWith(2), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=[2, [a, b], [a, b]]");
    }

    @Test
    void testFormatAllHandleObjectCorrectly() {
        // Given:
        final List<Object> arguments = list(new Object());

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).matches("c0=java.lang.Object@[0-9a-f]+");
    }

    @Test
    void testFormatAllHandleListsCorrectly() {
        // Given:
        final List<Object> arguments = list(list("test", 1, 1723940567289346512L), 3);

        // When:
        String result = underTest.formatAll(paramsWith(2), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=[test, 1, 1723940567289346512], i1=3");
    }

    @Test
    void testFormatAllHandleEnumsCorrectly() {
        // Given:
        final List<Object> arguments = list(Thread.State.RUNNABLE);

        // When:
        String result = underTest.formatAll(paramsWith(1), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=RUNNABLE");
    }

    @Test
    void testFormatAllHandleComplexExampleCorrectly() {
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
        String result = underTest.formatAll(paramsWith(4), arguments);

        // Then:
        assertThat(result).isEqualTo("c0=" + now.toString() + ", i1=3.5, l2=1|2|3, d3=src/main/java/com/tngtech");
    }

    // -- helper methods -----------------------------------------------------------------------------------------------

    private static class TestToString {
        private final String toString;

        TestToString(String toString) {
            this.toString = toString;
        }

        @Override
        public String toString() {
            return toString;
        }
    }

    @SuppressWarnings("unused")
    void tenParamMethod(char c0, int i1, long l2, double d3, String s4, char c5, int i6, long l7, double d8, float f9) {
        // nothing to do
    }

    private List<Object> list(Object first, Object... remaining) {
        List<Object> result = new ArrayList<>();
        result.add(first);
        for (Object object : remaining) {
            result.add(object);
        }
        return result;
    }

    private Parameter[] paramsWith(int size) {
        Parameter[] parameters = tenParamMethod.getParameters();
        if (size > parameters.length) {
            fail("Max '%d' parameters are available, requested '%d'.", parameters.length, size);
        }
        return Arrays.copyOf(parameters, size);
    }

    public String getTestCapturedLog() {
        customLogHandler.flush();
        return logCapturingStream.toString();
    }
}
