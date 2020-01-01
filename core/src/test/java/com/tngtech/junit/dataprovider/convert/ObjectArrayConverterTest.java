package com.tngtech.junit.dataprovider.convert;

import static org.assertj.core.api.Assertions.assertThat;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class ObjectArrayConverterTest {

    @SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification = "Mockito rule needs no further configuration")
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    private ObjectArrayConverter underTest;

    @Test
    public void testConvertShouldReturnSameObjectArrayWithoutVarargs() {
        // Given:
        Object[] data = new Object[] { 1 };
        Class<?>[] parameterTypes = new Class<?>[] { int.class };

        // When:
        Object[] result = underTest.convert(data, false, parameterTypes);

        // Then:
        assertThat(result).isEqualTo(data);
    }

    @Test
    public void testConvertShouldCreateEmptyVarargsArrayForMissingOnlyVarargsArgument() {
        // Given:
        Object[] data = new Object[0];
        Class<?>[] parameterTypes = new Class<?>[] { int[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).containsExactly(new int[0]);
    }

    @Test
    public void testConvertShouldHandleNullElementAsSingleElement() {
        // Given:
        Object[] data = new Object[] { null };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, int[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).containsExactly(null, new int[0]);
    }

    @Test
    public void testConvertShouldCreateEmptyVarargsArrayForLastMissingVarargsArgument() {
        // Given:
        Object[] data = new Object[] { "test" };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, int[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).containsExactly("test", new int[0]);
    }

    @Test
    public void testConvertShouldCreateVarargsArrayForOneOnlyVarargsArguments() {
        // Given:
        Object[] data = new Object[] { 1.0 };
        Class<?>[] parameterTypes = new Class<?>[] { double[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).containsExactly(new double[] { 1.0 });
    }

    @Test
    public void testConvertShouldCreateVarargsArrayForOneLastVarargsArguments() {
        // Given:
        Object[] data = new Object[] { 'a', (byte) 2, 1.0 };
        Class<?>[] parameterTypes = new Class<?>[] { char.class, byte.class, double[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).containsExactly('a', (byte) 2, new double[] { 1.0 });
    }

    @Test
    public void testConvertShouldCreateVarargsArrayForMultipleOnlyVarargsArguments() {
        // Given:
        Object[] data = new Object[] { 1l, 2l, 3l };
        Class<?>[] parameterTypes = new Class<?>[] { long[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).containsExactly(new long[] { 1, 2, 3 });
    }

    @Test
    public void testConvertShouldCreateVarargsArrayForMultipleLastVarargsArguments() {
        // Given:
        Object[] data = new Object[] { "foobar", 1l, 2l, 3l };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, long[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).containsExactly("foobar", new long[] { 1, 2, 3 });
    }

    @Test
    public void testConvertShouldForwardArrayIfVarargsArrayIsAlreadyGivenForSingleArgument() {
        // Given:
        Object[] data = new Object[] { new String[] { "a", "z" } };
        Class<?>[] parameterTypes = new Class<?>[] { String[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).isEqualTo(data);
    }

    @Test
    public void testConvertShouldForwardArrayIfVarargsArrayIsAlreadyGivenForMultipleArguments() {
        // Given:
        Object[] data = new Object[] { 2, new boolean[] { false, true } };
        Class<?>[] parameterTypes = new Class<?>[] { int.class, boolean[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).isEqualTo(data);
    }

    @Test
    public void testConvertShouldForwardArrayIfVarargsIsArrayOfArray() {
        // Given:
        Object[] data = new Object[] { new int[][] { { 1, 2 }, { 3, 4 } } };
        Class<?>[] parameterTypes = new Class<?>[] { int[][].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).isEqualTo(data);
    }

    @Test
    public void testConvertShouldNotForwardArrayIfVarargsIsArrayOfArray() {
        // Given:
        Object[] data = new Object[] { new int[] { 1, 2 } };
        Class<?>[] parameterTypes = new Class<?>[] { int[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).isEqualTo(new int[][] { { 1, 2 } });
    }
}
