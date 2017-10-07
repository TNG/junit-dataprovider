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
                "Object[] dataprovider just supports single argument test method but found 0 parameters");

        // When:
        underTest.convert(data, false, parameterTypes);

        // Then: expect exception
    }

    @Test
    public void testConvertShouldThrowIllegalArgumentExceptionIfParameterTypesSizeGreaterThanOne() {
        // Given:
        Object data = 2L;
        Class<?>[] parameterTypes = new Class<?>[] { int.class, String.class, Long.class };

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(
                "Object[] dataprovider just supports single argument test method but found 3 parameters");

        // When:
        underTest.convert(data, false, parameterTypes);

        // Then: expect exception
    }

    @Test
    public void testConvertShouldThrowIllegalArgumentExceptionIfVarargsIsTrue() {
        // Given:
        Object data = 3.0;
        Class<?>[] parameterTypes = new Class<?>[] { double.class };

        expectedException.expect(IllegalArgumentException.class);
        expectedException
                .expectMessage("Object[] dataprovider and single parameter test method does not support varargs");

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
