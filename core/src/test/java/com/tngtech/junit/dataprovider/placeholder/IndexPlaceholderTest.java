package com.tngtech.junit.dataprovider.placeholder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

import com.tngtech.junit.dataprovider.testutils.Methods;

public class IndexPlaceholderTest {

    private final IndexPlaceholder underTest = new IndexPlaceholder();

    @Test
    public void testProcessShouldReplacePlaceholder() {
        // Given:
        final int index = 42;

        ReplacementData data = ReplacementData.of(Methods.anyMethod(), index, Arrays.<Object>asList(0));

        // When:
        String result = underTest.process(data, "%i");

        // Then:
        assertThat(result).isEqualTo(String.valueOf(index));
    }
}
