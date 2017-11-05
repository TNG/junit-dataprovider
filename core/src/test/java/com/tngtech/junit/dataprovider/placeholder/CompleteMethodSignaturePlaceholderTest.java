package com.tngtech.junit.dataprovider.placeholder;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;

import com.tngtech.junit.dataprovider.testutils.Methods;

public class CompleteMethodSignaturePlaceholderTest {

    private final CompleteMethodSignaturePlaceholder underTest = new CompleteMethodSignaturePlaceholder();

    @Test
    public void testProcessShouldReplacePlaceholder() {
        // Given:
        final Method method = Methods.anyMethod();

        ReplacementData data = ReplacementData.of(method, 0, Arrays.<Object>asList(0));

        // When:
        String result = underTest.process(data, "%cm");

        // Then:
        assertThat(result).isEqualTo(method.toString());
    }
}
