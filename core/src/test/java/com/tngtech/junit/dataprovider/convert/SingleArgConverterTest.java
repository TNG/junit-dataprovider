package com.tngtech.junit.dataprovider.convert;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class SingleArgConverterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    private SingleArgConverter underTest;

    @Test
    public void testConvertShouldThrowIllegalArgumentExceptionIfParameterTypesSizeIsZero() {
        // Given:
        Object data = 1;
        Class<?>[] parameterTypes = new Class<?>[] {};

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(
                "Object[] dataprovider must at least have a single argument for the dataprovider but found no parameters");

        // When:
        underTest.convert(data, false, parameterTypes);

        // Then: expect exception
    }

    @Test
    public void testConvertShouldReturnArgumentIfParameterTypesSizeIsEvenGreaterThanOne() {
        // Given:
        Object data = 2;
        Class<?>[] parameterTypes = new Class<?>[] { long.class, String.class, Long.class };

        // When:
        Object[] result = underTest.convert(data, false, parameterTypes);

        // Then:
        assertThat(result).containsExactly(data);
    }

    @Test
    public void testConvertShouldThrowIllegalArgumentExceptionIfVarargsIsTrue() {
        // Given:
        Object data = 3.0;
        Class<?>[] parameterTypes = new Class<?>[] { double.class };

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Object[] dataprovider does not support varargs");

        // When:
        underTest.convert(data, true, parameterTypes);

        // Then: expect exception
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
