package com.tngtech.java.junit.dataprovider.internal.convert;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;

@RunWith(MockitoJUnitRunner.class)
public class SingleArgConverterTest extends BaseTest {

    @InjectMocks
    private SingleArgConverter underTest;

    @Test(expected = IllegalArgumentException.class)
    public void testConvertShouldThrowIllegalArgumentExceptionIfParameterTypesSizeIsZero() {
        // Given:
        Object data = 1;
        Class<?>[] parameterTypes = new Class<?>[] {};

        // When:
        underTest.convert(data, false, parameterTypes);

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertShouldThrowIllegalArgumentExceptionIfParameterTypesSizeGreaterThanOne() {
        // Given:
        Object data = 2L;
        Class<?>[] parameterTypes = new Class<?>[] { int.class, String.class, Long.class };

        // When:
        underTest.convert(data, false, parameterTypes);

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertShouldThrowIllegalArgumentExceptionIfVarargsIsTrue() {
        // Given:
        Object data = 3.0;
        Class<?>[] parameterTypes = new Class<?>[] { double.class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).containsExactly(new int[0]);
    }

    @Test
    public void testConvertShouldWrapInputIntoArryAndReturnItIfPreconditionsMet() {
        // Given:
        Object data = "4";
        Class<?>[] parameterTypes = new Class<?>[] { String.class };

        // When:
        Object[] result = underTest.convert(data, false, parameterTypes);

        // Then:
        assertThat(result).containsExactly("4");
    }
}
