package com.tngtech.junit.dataprovider.placeholder;

import static org.assertj.core.api.Assertions.assertThat;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class BasePlaceholderTest {

    private static class TestPlaceholder extends BasePlaceholder {
        private final String replacement;

        private ReplacementData data;

        public TestPlaceholder(String placeHolderRegex, String replacement) {
            super(placeHolderRegex);
            this.replacement = replacement;
        }

        @Override
        protected String getReplacementFor(String placeholder, ReplacementData data) {
            this.data = data;
            return (replacement == null) ? placeholder : replacement;
        }
    }

    @SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification = "Mockito rule needs no further configuration")
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ReplacementData data;

    @Test
    public void testProcessShouldForwardDataToGetReplacementFor() {
        // Given:
        TestPlaceholder underTest = new TestPlaceholder("%s", "test");

        // When:
        underTest.process(data, "%s");

        // Then:
        assertThat(underTest.data).isSameAs(data);
    }

    @Test
    public void testProcessShouldReplaceNothingForNotMatchingPlaceholder() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "##foo##");

        // When:
        String result = underTest.process(null, "%d");

        // Then:
        assertThat(result).isEqualTo("%d");
    }

    @Test
    public void testProcessShouldReplaceSinglePlaceholder() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "##foo##");

        // When:
        String result = underTest.process(null, "%s");

        // Then:
        assertThat(result).isEqualTo("##foo##");
    }

    @Test
    public void testProcessShouldReplaceSinglePlaceholderSurroundedByText() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "##bar##");

        // When:
        String result = underTest.process(null, "Test %s()");

        // Then:
        assertThat(result).isEqualTo("Test ##bar##()");
    }

    @Test
    public void testProcessShouldReplaceSinglePlaceholderSurroundedByOtherPlaceholders() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "##baz##");

        // When:
        String result = underTest.process(null, "%a%s%b");

        // Then:
        assertThat(result).isEqualTo("%a##baz##%b");
    }

    @Test
    public void testProcessShouldReplaceMultiplePlaceholders() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "##bla##");

        // When:
        String result = underTest.process(null, "ss%ss%ss%ss%");

        // Then:
        assertThat(result).isEqualTo("ss##bla##s##bla##s##bla##s%");
    }

    @Test
    public void testProcessShouldReplacePlaceholderNotCauseAnStackOverflowExceptionIfPlaceholderIsReplacedbyItself() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "%s");

        // When:
        String result = underTest.process(null, "%s");

        // Then:
        assertThat(result).isEqualTo("%s");
    }

    @Test
    public void testProcessShouldReplacePlaceholderNotRecursivelyForSimpleFormatPattern() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "1234%s");

        // When:
        String result = underTest.process(null, "%s");

        // Then:
        assertThat(result).isEqualTo("1234%s");
    }

    @Test
    public void testProcessShouldReplacePlaceholderNotRecursivelyForSimpleFormatPatternContainingPlaceholderTwice() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "1234%s");

        // When:
        String result = underTest.process(null, "%s%s");

        // Then:
        assertThat(result).isEqualTo("1234%s1234%s");
    }

    @Test
    public void testProcessShouldReplacePlaceholderNotRecursivelyForComplexFormatPattern() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "abc%s%");

        // When:
        String result = underTest.process(null, "%ss%s");

        // Then:
        assertThat(result).isEqualTo("abc%s%sabc%s%");
    }

    @Test
    public void testProcessShouldHandleDollarSignsCorrectly() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "$");

        // When:
        String result = underTest.process(null, "%s");

        // Then:
        assertThat(result).isEqualTo("$");
    }

    @Test
    public void testProcessShouldHandleBackslashesCorrectly() {
        // Given:
        BasePlaceholder underTest = new TestPlaceholder("%s", "\\");

        // When:
        String result = underTest.process(null, "%s");

        // Then:
        assertThat(result).isEqualTo("\\");
    }
}
