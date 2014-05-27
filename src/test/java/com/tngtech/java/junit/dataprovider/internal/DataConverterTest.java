package com.tngtech.java.junit.dataprovider.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;

@RunWith(MockitoJUnitRunner.class)
public class DataConverterTest extends BaseTest {

    @InjectMocks
    private DataConverter underTest;

    @Test
    public void testCanConvertShouldReturnFalseIfTypeIsNull() {
        // Given:

        // When:
        boolean result = underTest.canConvert(null);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testCanConvertShouldReturnFalseIfTypeIsObject() {
        // Given:

        // When:
        boolean result = underTest.canConvert(Object.class);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testCanConvertShouldReturnFalseIfTypeIsString() {
        // Given:

        // When:
        boolean result = underTest.canConvert(String.class);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testCanConvertShouldReturnFalseIfTypeIsList() {
        // Given:
        Type type = List.class;

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testCanConvertShouldReturnFalseIfTypeIsListOfObject() {
        // Given:
        Type type = getMethod("methodReturningListOfObject").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testCanConvertShouldReturnFalseIfTypeIsListOfIterable() {
        // Given:
        Type type = getMethod("methodReturningListOfIterableOfObject").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testCanConvertShouldReturnFalseIfTypeIsIterableOfIterable() {
        // Given:
        Type type = getMethod("methodReturningIterableOfIterableOfObject").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testCanConvertShouldReturnFalseIfTypeIsSetOfSet() {
        // Given:
        Type type = getMethod("methodReturningSetOfSetOfObject").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testCanConvertShouldReturnFalseIfTypeIsTwoArgList() {
        // Given:
        Type type = getMethod("methodReturningTwoArgListOfListsOfObject").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsObjectArrayArray() {
        // Given:
        Type type = getMethod("methodReturningObjectArrayArray").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsListListObject() {
        // Given:
        Type type = getMethod("methodReturningListOfListOfObject").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsSubListSubListObject() {
        // Given:
        Type type = getMethod("methodReturningSubListOfSubListOfObject").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void testConvertValueShouldThrowNullPointerExceptionIfParameterTypesIsNull() {
        // Given:
        final Object data = null;
        final Class<?>[] parameterTypes = null;

        // When:
        underTest.convert(data, parameterTypes);

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertValueShouldThrowIllegalArgumentExceptionIfParameterTypesIsEmpty() {
        // Given:
        final Object data = null;
        final Class<?>[] parameterTypes = new Class<?>[0];

        // When:
        underTest.convert(data, parameterTypes);

        // Then: expect exception
    }

    @Test(expected = ClassCastException.class)
    public void testConvertValueShouldThrowClassCastExceptionIfDataIsNull() {
        // Given:
        final Object data = null;
        final Class<?>[] parameterTypes = new Class<?>[] { String.class };

        // When:
        underTest.convert(data, parameterTypes);

        // Then: expect exception
    }

    @Test(expected = ClassCastException.class)
    public void testConvertValueShouldThrowClassCastExceptionIfDataIsNotConvertable() {
        // Given:
        final Object data = "not convertable";
        final Class<?>[] parameterTypes = new Class<?>[] { String.class };

        // When:
        underTest.convert(data, parameterTypes);

        // Then: expect exception
    }

    @Test
    public void testConvertValueShouldReturnOneElementForObjectArrayArrayWithOneElement() {
        // Given:
        final Object[][] data = new Object[][] { { 1 } };
        final Class<?>[] parameterTypes = new Class<?>[] { int.class };

        // When:
        List<Object[]> result = underTest.convert(data, parameterTypes);

        // Then:
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(data[0]);
    }

    @Test
    public void testConvertValueShouldReturnMultipleElementsForObjectArrayArrayWithMultipleElements() {
        // Given:
        final Object[][] data = new Object[][] { { "11", 22L, 3.3 }, { "44", 55L, 6.6 }, { "77", 88L, 9.9 } };
        final Class<?>[] parameterTypes = new Class<?>[] { String.class, long.class, double.class };

        // When:
        List<Object[]> result = underTest.convert(data, parameterTypes);

        // Then:
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(data[0]);
        assertThat(result.get(1)).isEqualTo(data[1]);
        assertThat(result.get(2)).isEqualTo(data[2]);
    }

    @Test
    public void testConvertValueShouldReturnOneElementForListOfListOfObjectWithOneElement() {
        // Given:
        @SuppressWarnings("unchecked")
        final List<List<Character>> data = list(list('a'));
        final Class<?>[] parameterTypes = new Class<?>[] { char.class };

        // When:
        List<Object[]> result = underTest.convert(data, parameterTypes);

        // Then:
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(data.get(0).toArray());
    }

    @Test
    public void testConvertValueShouldReturnMultipleElementsForListOfListOfObjectWithMultipleElements() {
        // Given:
        @SuppressWarnings("unchecked")
        final List<List<?>> data = list(this.<Object> list('x', "foo"), list('y', "bar"), list('z', "baz"));
        final Class<?>[] parameterTypes = new Class<?>[] { char.class, String.class };

        // When:
        List<Object[]> result = underTest.convert(data, parameterTypes);

        // Then:
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(data.get(0).toArray());
        assertThat(result.get(1)).isEqualTo(data.get(1).toArray());
        assertThat(result.get(2)).isEqualTo(data.get(2).toArray());
    }

    @Test(expected = Error.class)
    public void testConvertShouldThrowErrorIfLengthOfSplitDataAndTargetTypesDiffer() {
        // Given:
        String[] data = new String[] { "1, 2" };
        Class<?>[] parameterTypes = new Class[] { int.class };

        // When:
        underTest.convert(data, parameterTypes);

        // Then: expect exception
    }

    @Test
    public void testConvertValueShouldReturnOneElementForStringArrayWithOneElement() {
        // Given:
        final String[] data = new String[] { "test, true" };
        final Class<?>[] parameterTypes = new Class<?>[] { String.class, boolean.class };

        // When:
        List<Object[]> result = underTest.convert(data, parameterTypes);

        // Then:
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(new Object[] { "test", true });
    }

    @Test
    public void testConvertValueShouldReturnMultipleElementsForStringArrayWithMultipleElements() {
        // Given:
        final String[] data = new String[] { "1, 2, 3, 4.0, e", "6, 7, 8, 9.0, i" };
        final Class<?>[] parameterTypes = new Class<?>[] { byte.class, int.class, long.class, double.class, char.class };

        // When:
        List<Object[]> result = underTest.convert(data, parameterTypes);

        // Then:
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(new Object[] { (byte) 1, 2, 3l, 4.0, 'e' });
        assertThat(result.get(1)).isEqualTo(new Object[] { (byte) 6, 7, 8l, 9.0, 'i' });
    }

    @Test
    public void testGetParametersForShouldCorrectlyParseAllPrimitiveTypes() {
        // Given:
        String data = "true,1,c,2,3,4,5.5,6.6";
        Class<?>[] parameterTypes = new Class[] { boolean.class, byte.class, char.class, short.class, int.class,
                long.class, float.class, double.class };

        // When:
        Object[] result = underTest.getParametersFor(data, parameterTypes, 10);

        // Then:
        assertThat(result).isEqualTo(new Object[] { true, (byte) 1, 'c', (short) 2, 3, 4L, 5.5f, 6.6d });
    }

    @Test
    public void testGetParametersForShouldCorrectlyParseAllPrimitiveTypesAsJavaString() {
        // Given:
        String data = "-5,2014l,-1.234567f,-901e-3";
        Class<?>[] parameterTypes = new Class[] { int.class, long.class, float.class, double.class };

        // When:
        Object[] result = underTest.getParametersFor(data, parameterTypes, 11);

        // Then:
        assertThat(result).isEqualTo(new Object[] { -5, 2014l, -1.234567f, -0.901d });
    }

    @Test
    public void testGetParametersForShouldCorrectlyParseAllPrimitiveTypesEvenIfUntrimmed() {
        // Given:
        String data = "   false   ,    11    ,    z    ,  22       ,   33   ,44      ,   55.55     ,  66.66     ";
        Class<?>[] parameterTypes = new Class[] { boolean.class, byte.class, char.class, short.class, int.class,
                long.class, float.class, double.class };

        // When:
        Object[] result = underTest.getParametersFor(data, parameterTypes, 12);

        // Then:
        assertThat(result).isEqualTo(new Object[] { false, (byte) 11, 'z', (short) 22, 33, 44L, 55.55f, 66.66d });
    }

    @Test
    public void testGetParametersForShouldCorrectlyTrimNonSpaceWhitespaceChars() {
        // Given:
        String data = "\n-1f\n,\r-2\r,\t3.0d\t";

        Class<?>[] parameterTypes = new Class[] { float.class, int.class, double.class };

        // When:
        Object[] result = underTest.getParametersFor(data, parameterTypes, 20);

        // Then:
        assertThat(result).isEqualTo(new Object[] { -1f, -2, 3d });
    }

    @Test
    public void testGetParametersForShouldNotTrimNonBreakingSpace() {
        // Given:
        String data = "\u00A0test\u00A0";

        Class<?>[] parameterTypes = new Class[] { String.class };

        // When:
        Object[] result = underTest.getParametersFor(data, parameterTypes, 21);

        // Then:
        assertThat(result).isEqualTo(new Object[] { "\u00A0test\u00A0" });
    }

    @Test
    public void testGetParametersForShouldCorrectlyHandleLeadingEmptyString() {
        // Given:
        String data = ",true";
        Class<?>[] parameterTypes = new Class[] { String.class, boolean.class };

        // When:
        Object[] result = underTest.getParametersFor(data, parameterTypes, 30);

        // Then:
        assertThat(result).isEqualTo(new Object[] { "", true });
    }

    @Test
    public void testGetParametersForShouldCorrectlyHandleTrailingEmptyString() {
        // Given:
        String data = "1,";
        Class<?>[] parameterTypes = new Class[] { int.class, String.class };

        // When:
        Object[] result = underTest.getParametersFor(data, parameterTypes, 31);

        // Then:
        assertThat(result).isEqualTo(new Object[] { 1, "" });
    }

    @Test(expected = Error.class)
    public void testGetParametersForShouldThrowErrorIfCharHasNotLengthOne() {
        // Given:
        String data = "noChar";
        Class<?>[] parameterTypes = new Class[] { char.class };

        // When:
        underTest.getParametersFor(data, parameterTypes, 40);

        // Then: expect exception
    }

    @Test(expected = Error.class)
    public void testGetParametersForShouldThrowErrorForUnsupportedTargetType() {
        // Given:
        String data = "noObject";
        Class<?>[] parameterTypes = new Class[] { Object.class };

        // When:
        underTest.getParametersFor(data, parameterTypes, 41);

        // Then: expect exception
    }

    @Test
    public void testGetParametersForShouldCorrectlyParseEnum() {
        // Given:
        String data = " VAL1,  VAL2 ";
        Class<?>[] parameterTypes = new Class[] { TestEnum.class, TestEnum.class };

        // When:
        Object[] result = underTest.getParametersFor(data, parameterTypes, 50);

        // Then:
        assertThat(result).isEqualTo(new Object[] { TestEnum.VAL1, TestEnum.VAL2 });
    }

    @Test(expected = Error.class)
    public void testGetParametersForShouldThrowErrorIfEnumValueIsInvalid() {
        // Given:
        String data = "UNKNOW_ENUM_VALUE";
        Class<?>[] parameterTypes = new Class[] { TestEnum.class };

        // When:
        underTest.getParametersFor(data, parameterTypes, 51);

        // Then: expect exception
    }

    @Test
    public void testGetParametersForShouldCorrectlyParseAllPrimitiveWrapperTypes() {
        // Given:
        String data = "true,1,c,2,3,4,5.5,6.6";
        Class<?>[] parameterTypes = new Class[] { Boolean.class, Byte.class, Character.class, Short.class,
                Integer.class, Long.class, Float.class, Double.class };

        // When:
        Object[] result = underTest.getParametersFor(data, parameterTypes, 60);

        // Then:
        assertThat(result).isEqualTo(
                new Object[] { Boolean.TRUE, Byte.valueOf((byte) 1), Character.valueOf('c'), Short.valueOf((short) 2),
                        Integer.valueOf(3), Long.valueOf(4L), Float.valueOf(5.5f), Double.valueOf(6.6d) });
    }

    @Test
    public void testGetParametersForShouldCorrectlyParseNullValue() {
        // Given:
        String data = "null, null  ";
        Class<?>[] parameterTypes = new Class[] { Boolean.class, String.class };

        // When:
        Object[] result = underTest.getParametersFor(data, parameterTypes, 70);

        // Then:
        assertThat(result).isEqualTo(new Object[] { null, null });
    }

    // -- helper methods -----------------------------------------------------------------------------------------------

    public static List<Object> methodReturningListOfObject() {
        return null;
    }

    public static List<Iterable<Object>> methodReturningListOfIterableOfObject() {
        return null;
    }

    public static Iterable<Iterable<Object>> methodReturningIterableOfIterableOfObject() {
        return null;
    }

    public static Set<Set<Object>> methodReturningSetOfSetOfObject() {
        return null;
    }

    @SuppressWarnings("serial")
    private static class TwoArgList<A, B> extends ArrayList<A> {
        // not required for now :-)
    }

    public static TwoArgList<List<Object>, List<Object>> methodReturningTwoArgListOfListsOfObject() {
        return null;
    }

    public static Object[][] methodReturningObjectArrayArray() {
        return null;
    }

    public static List<List<Object>> methodReturningListOfListOfObject() {
        return null;
    }

    @SuppressWarnings("serial")
    private static class SubList<A> extends ArrayList<A> {
        // not required for now :-)
    }

    public static SubList<SubList<Object>> methodReturningSubListOfSubListOfObject() {
        return null;
    }
}
