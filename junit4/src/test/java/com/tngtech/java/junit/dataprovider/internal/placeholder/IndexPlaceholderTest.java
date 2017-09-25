package com.tngtech.java.junit.dataprovider.internal.placeholder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;

@RunWith(MockitoJUnitRunner.class)
public class IndexPlaceholderTest extends BaseTest {

    @InjectMocks
    private IndexPlaceholder underTest;

    @Test
    public void testProcessShouldReplacePlaceholder() {
        // Given:
        final int index = 42;

        underTest.setContext(anyMethod(), index, new Object[] { 0 });

        // When:
        String result = underTest.process("%i");

        // Then:
        assertThat(result).isEqualTo(String.valueOf(index));
    }
}
