package com.tngtech.java.junit.dataprovider.internal.convert;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;

@RunWith(MockitoJUnitRunner.class)
public class ObjectArrayConverterTest extends BaseTest {

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
        assertThat(result).isEqualTo(new Object[] { new int[0] });
    }

    @Test
    public void testConvertShouldCreateEmptyVarargsArrayForLastMissingVarargsArgument() {
        // Given:
        Object[] data = new Object[] { "test" };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, int[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).isEqualTo(new Object[] { "test", new int[0] });
    }

    @Test
    public void testConvertShouldCreateVarargsArrayForOneOnlyVarargsArguments() {
        // Given:
        Object[] data = new Object[] { 1.0 };
        Class<?>[] parameterTypes = new Class<?>[] { double[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).isEqualTo(new Object[] { new double[] { 1.0 } });
    }

    @Test
    public void testConvertShouldCreateVarargsArrayForOneLastVarargsArguments() {
        // Given:
        Object[] data = new Object[] { 'a', (byte) 2, 1.0 };
        Class<?>[] parameterTypes = new Class<?>[] { char.class, byte.class, double[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).isEqualTo(new Object[] { 'a', (byte) 2, new double[] { 1.0 } });
    }

    @Test
    public void testConvertShouldCreateVarargsArrayForMultipleOnlyVarargsArguments() {
        // Given:
        Object[] data = new Object[] { 1l, 2l, 3l };
        Class<?>[] parameterTypes = new Class<?>[] { long[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).isEqualTo(new Object[] { new long[] { 1, 2, 3 } });
    }

    @Test
    public void testConvertShouldCreateVarargsArrayForMultipleLastVarargsArguments() {
        // Given:
        Object[] data = new Object[] { "foobar", 1l, 2l, 3l };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, long[].class };

        // When:
        Object[] result = underTest.convert(data, true, parameterTypes);

        // Then:
        assertThat(result).isEqualTo(new Object[] { "foobar", new long[] { 1, 2, 3 } });
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

    @Test(expected = NullPointerException.class)
    public void testCheckIfArgumentsMatchParameterTypesShouldThrowNullPointerExceptionIfArgumentsIsNull() {
        // Given:

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(null, new Class<?>[0]);

        // Then: expect exception
    }

    @Test(expected = NullPointerException.class)
    public void testCheckIfArgumentsMatchParameterTypesShouldThrowNullPointerExceptionIfParameterTypesIsNull() {
        // Given:

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(new Object[0], null);

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckIfArgumentsMatchParameterTypesShouldThrowIllegalArgumentExceptionIfLengthOfArgumentsAndParameterTypesDoesNotMatch() {
        // Given:
        Object[] arguments = new Object[0];
        Class<?>[] parameterTypes = new Class<?>[] { int.class, String.class, boolean.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckIfArgumentsMatchParameterTypesShouldThrowIllegalArgumentExceptionIfSingleArgumentIsNotAssignableToParameterType() {
        // Given:
        Object[] arguments = new Object[] { "1" };
        Class<?>[] parameterTypes = new Class<?>[] { int.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckIfArgumentsMatchParameterTypesShouldThrowIllegalArgumentExceptionIfAnyArgumentTypeIsNotAssignableToParameterType() {
        // Given:
        Object[] arguments = new Object[] { 2, "2", 2l };
        Class<?>[] parameterTypes = new Class<?>[] { int.class, String.class, boolean.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: expect exception
    }

    @Test
    public void testCheckIfArgumentsMatchParameterTypesShouldNotThrowExceptionIfSingleArgumentTypeEqualsParameterType() {
        // Given:
        Object[] arguments = new Object[] { Character.valueOf('a') };
        Class<?>[] parameterTypes = new Class<?>[] { Character.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: no exception
    }

    @Test
    public void testCheckIfArgumentsMatchParameterTypesShouldNotThrowExceptionIfEveryArgumentTypeEqualsParameterTypesExactly() {
        // Given:
        Object[] arguments = new Object[] { "b", Boolean.FALSE };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, Boolean.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: no exception
    }

    @Test
    public void testCheckIfArgumentsMatchParameterTypesShouldNotThrowExceptionIfArgumentsIsAreAssignableToParameterTypes() {
        // Given:
        Object[] arguments = new Object[] { Long.valueOf(1l), Integer.valueOf(2) };
        Class<?>[] parameterTypes = new Class<?>[] { Number.class, Number.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: no exception
    }

    @Test
    public void testCheckIfArgumentsMatchParameterTypesShouldNotThrowExceptionIfArgumentsAreWrappedPrimitivesOfParameterTypes() {
        // Given:
        Object[] arguments = new Object[] { Boolean.FALSE, Character.valueOf('a'), Byte.valueOf((byte) 2),
                Short.valueOf((short) 3), Integer.valueOf(4), Long.valueOf(5l), Float.valueOf(6.6f),
                Double.valueOf(7.7) };
        Class<?>[] parameterTypes = new Class<?>[] { boolean.class, char.class, byte.class, short.class, int.class,
                long.class, float.class, double.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: no exception
    }

    @Test
    public void testCheckIfArgumentsMatchParameterTypesShouldBeAwareOfWideningPrimitiveConversionsOfByte() {
        // Given:
        Object[] arguments = new Object[] { (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5 };
        Class<?>[] parameterTypes = new Class<?>[] { short.class, int.class, long.class, float.class, double.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: no exception
    }

    @Test
    public void testCheckIfArgumentsMatchParameterTypesShouldBeAwareOfWideningPrimitiveConversionsOfShort() {
        // Given:
        Object[] arguments = new Object[] { (short) 1, (short) 2, (short) 3, (short) 4 };
        Class<?>[] parameterTypes = new Class<?>[] { int.class, long.class, float.class, double.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: no exception
    }

    @Test
    public void testCheckIfArgumentsMatchParameterTypesShouldBeAwareOfWideningPrimitiveConversionsOfChar() {
        // Given:
        Object[] arguments = new Object[] { (char) 1, (char) 2, (char) 3, (char) 4 };
        Class<?>[] parameterTypes = new Class<?>[] { int.class, long.class, float.class, double.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: no exception
    }

    @Test
    public void testCheckIfArgumentsMatchParameterTypesShouldBeAwareOfWideningPrimitiveConversionsOfInt() {
        // Given:
        Object[] arguments = new Object[] { 1, 2, 3 };
        Class<?>[] parameterTypes = new Class<?>[] { long.class, float.class, double.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: no exception
    }

    @Test
    public void testCheckIfArgumentsMatchParameterTypesShouldBeAwareOfWideningPrimitiveConversionsOfFloat() {
        // Given:
        Object[] arguments = new Object[] { 1.1f };
        Class<?>[] parameterTypes = new Class<?>[] { double.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: no exception
    }

    @Test
    public void testCheckIfArgumentsMatchParameterTypesShouldBeAwareOfWideningPrimitiveConversionsOfLong() {
        // Given:
        Object[] arguments = new Object[] { 1l, 2l };
        Class<?>[] parameterTypes = new Class<?>[] { float.class, double.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: no exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckIfArgumentsMatchParameterTypesShouldThrowExceptionForNonWideningConversionsOfLong() {
        // Given:
        Object[] arguments = new Object[] { (long) 1 };

        Class<?>[] parameterTypes = new Class<?>[] { int.class };

        // When:
        underTest.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);

        // Then: expect exception
    }
}
