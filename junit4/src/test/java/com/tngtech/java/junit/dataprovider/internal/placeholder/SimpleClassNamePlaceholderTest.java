package com.tngtech.java.junit.dataprovider.internal.placeholder;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;

@RunWith(MockitoJUnitRunner.class)
public class SimpleClassNamePlaceholderTest extends BaseTest {

    @InjectMocks
    private SimpleClassNamePlaceholder underTest;

    @Test
    public void testProcessShouldReplacePlaceholder() {
        // Given:
        final Method method = anyMethod();

        underTest.setContext(method, 0, new Object[] { 0 });

        // When:
        String result = underTest.process("%c");

        // Then:
        assertThat(result).isEqualTo(method.getDeclaringClass().getSimpleName());
    }
}
