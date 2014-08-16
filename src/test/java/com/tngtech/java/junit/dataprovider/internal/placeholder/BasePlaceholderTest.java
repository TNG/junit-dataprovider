package com.tngtech.java.junit.dataprovider.internal.placeholder;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

import com.tngtech.java.junit.dataprovider.BaseTest;

public class BasePlaceholderTest extends BaseTest {

    private static class TestPlaceholder extends BasePlaceholder {
        private final String replacement;

        public TestPlaceholder(String placeHolderRegex, String replacement) {
            super(placeHolderRegex);
            this.replacement = replacement;
        }

        @Override
        protected String getReplacementFor(String placeholder) {
            return (replacement == null) ? placeholder : replacement;
        }
    }

    @Test
    public void testSetContextShouldSetAllContextFields() {
        // Given:
        Method method = anyMethod();
        int index = 0;
        Object[] parameters = new Object[] { 1, 2, 3 };

        BasePlaceholder underTest = new TestPlaceholder("%s", "###");
        underTest.method = null;
        underTest.idx = Integer.MAX_VALUE;
        underTest.parameters = null;

        // When:
        underTest.setContext(method, index, parameters);

        // Then:
        assertThat(underTest.method).isEqualTo(method);
        assertThat(underTest.idx).isEqualTo(index);
        assertThat(underTest.parameters).isNotSameAs(parameters).isEqualTo(parameters);
    }

    @Test
    public void testProcessShouldReplaceNothingForNotMatchingPlaceholder() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "##foo##");

        // When:
        String result = underTest.process("%d");

        // Then:
        assertThat(result).isEqualTo("%d");
    }

    @Test
    public void testProcessShouldReplaceSinglePlaceholder() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "##foo##");

        // When:
        String result = underTest.process("%s");

        // Then:
        assertThat(result).isEqualTo("##foo##");
    }

    @Test
    public void testProcessShouldReplaceSinglePlaceholderSurroundedByText() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "##bar##");

        // When:
        String result = underTest.process("Test %s()");

        // Then:
        assertThat(result).isEqualTo("Test ##bar##()");
    }

    @Test
    public void testProcessShouldReplaceSinglePlaceholderSurroundedByOtherPlaceholders() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "##baz##");

        // When:
        String result = underTest.process("%a%s%b");

        // Then:
        assertThat(result).isEqualTo("%a##baz##%b");
    }

    @Test
    public void testProcessShouldReplaceMultiplePlaceholders() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "##bla##");

        // When:
        String result = underTest.process("ss%ss%ss%ss%");

        // Then:
        assertThat(result).isEqualTo("ss##bla##s##bla##s##bla##s%");
    }

    @Test
    public void testProcessShouldReplacePlaceholderNotCauseAnStackOverflowExceptionIfPlaceholderIsReplacedbyItself() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "%s");

        // When:
        String result = underTest.process("%s");

        // Then:
        assertThat(result).isEqualTo("%s");
    }

    @Test
    public void testProcessShouldReplacePlaceholderNotRecursivelyForSimpleFormatPattern() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "1234%s");

        // When:
        String result = underTest.process("%s");

        // Then:
        assertThat(result).isEqualTo("1234%s");
    }

    @Test
    public void testProcessShouldReplacePlaceholderNotRecursivelyForSimpleFormatPatternContainingPlaceholderTwice() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "1234%s");

        // When:
        String result = underTest.process("%s%s");

        // Then:
        assertThat(result).isEqualTo("1234%s1234%s");
    }

    @Test
    public void testProcessShouldReplacePlaceholderNotRecursivelyForComplexFormatPattern() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "abc%s%");

        // When:
        String result = underTest.process("%ss%s");

        // Then:
        assertThat(result).isEqualTo("abc%s%sabc%s%");
    }

    @Test
    public void testProcessShouldHandleDollarSignsCorrectly() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "$");

        // When:
        String result = underTest.process("%s");

        // Then:
        assertThat(result).isEqualTo("$");
    }

    @Test
    public void testProcessShouldHandleBackslashesCorrectly() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "\\");

        // When:
        String result = underTest.process("%s");

        // Then:
        assertThat(result).isEqualTo("\\");
    }
}
