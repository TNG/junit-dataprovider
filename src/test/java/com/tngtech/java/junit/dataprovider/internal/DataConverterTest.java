package com.tngtech.java.junit.dataprovider.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.internal.convert.ObjectArrayConverter;
import com.tngtech.java.junit.dataprovider.internal.convert.SingleArgConverter;
import com.tngtech.java.junit.dataprovider.internal.convert.StringConverter;

@RunWith(MockitoJUnitRunner.class)
public class DataConverterTest extends BaseTest {

    @InjectMocks
    private DataConverter underTest;

    @Mock
    private DataProvider dataProvider;

    @Mock
    private ObjectArrayConverter objectArrayConverter;

    @Mock
    private SingleArgConverter singleArgConverter;

    @Mock
    private StringConverter stringConverter;

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
    public void testCanConvertShouldReturnTrueIfTypeIsListOfObject() {
        // Given:
        Type type = getMethod("methodReturningListOfObject").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsIterableOfWildcard() {
        // Given:
        Type type = getMethod("methodReturningIterableOfWildcard").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsListOfWildcard() {
        // Given:
        Type type = getMethod("methodReturningListOfWildcard").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsIterableOfIterable() {
        // Given:
        Type type = getMethod("methodReturningIterableOfIterableOfT").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsSetOfSet() {
        // Given:
        Type type = getMethod("methodReturningSetOfSetOfWildcard").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
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
    public void testCanConvertShouldReturnTrueIfTypeIsObjectArray() {
        // Given:
        Type type = getMethod("methodReturningObjectArray").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsStringArray() {
        // Given:
        Type type = getMethod("methodReturningStringArray").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsListOfListOfObject() {
        // Given:
        Type type = getMethod("methodReturningListOfListOfObject").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsListOfListOfWildcard() {
        // Given:
        Type type = getMethod("methodReturningListOfListOfWildcard").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsListOfIterable() {
        // Given:
        Type type = getMethod("methodReturningListOfIterableOfNumber").getGenericReturnType();

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
    public void testConvertShouldThrowNullPointerExceptionIfParameterTypesIsNull() {
        // Given:
        Object data = null;
        Class<?>[] parameterTypes = null;

        // When:
        underTest.convert(data, false, parameterTypes, dataProvider);

        // Then: expect exception
    }

    @Test(expected = NullPointerException.class)
    public void testConvertShouldThrowNullPointerExceptionIfDataProviderIsNull() {
        // Given:
        Object data = null;
        Class<?>[] parameterTypes = new Class<?>[] { Object.class };

        // When:
        underTest.convert(data, false, parameterTypes, null);

        // Then: expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertShouldThrowIllegalArgumentExceptionIfParameterTypesIsEmpty() {
        // Given:
        Object data = null;
        Class<?>[] parameterTypes = new Class<?>[0];

        // When:
        underTest.convert(data, false, parameterTypes, dataProvider);

        // Then: expect exception
    }

    @Test(expected = ClassCastException.class)
    public void testConvertShouldThrowClassCastExceptionIfDataIsNull() {
        // Given:
        Object data = null;
        Class<?>[] parameterTypes = new Class<?>[] { String.class };

        // When:
        underTest.convert(data, false, parameterTypes, dataProvider);

        // Then: expect exception
    }

    @Test(expected = ClassCastException.class)
    public void testConvertShouldThrowClassCastExceptionIfDataIsNotConvertable() {
        // Given:
        Object data = "not convertable";
        Class<?>[] parameterTypes = new Class<?>[] { Integer.class };

        // When:
        underTest.convert(data, false, parameterTypes, dataProvider);

        // Then: expect exception
    }

    @Test
    public void testConvertShouldCallObjectArrayConverterOnlyOnceForObjectArrayArrayWithOneElement() {
        // Given:
        Object[][] data = new Object[][] { { 1 } };
        Class<?>[] parameterTypes = new Class<?>[] { int.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(1);
        verify(objectArrayConverter).convert(data[0], false, parameterTypes);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
    }

    @Test
    public void testConvertShouldCallObjectArrayConverterMultipleTimesForObjectArrayArrayWithMultipleElements() {
        // Given:
        Object[][] data = new Object[][] { { "11", 22L, 3.3 }, { "44", 55L, 6.6 }, { "77", 88L, 9.9 } };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, long.class, double.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(3);
        InOrder inOrder = inOrder(objectArrayConverter, singleArgConverter, stringConverter);
        inOrder.verify(objectArrayConverter).convert(data[0], false, parameterTypes);
        inOrder.verify(objectArrayConverter).convert(data[1], false, parameterTypes);
        inOrder.verify(objectArrayConverter).convert(data[2], false, parameterTypes);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
    }

    @Test
    public void testConvertShouldCallObjectArrayConverterOnlyOnceForListOfListOfObjectWithOneElement() {
        // Given:
        @SuppressWarnings("unchecked")
        List<List<Character>> data = list(list('a'));
        Class<?>[] parameterTypes = new Class<?>[] { char.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(1);
        verify(objectArrayConverter).convert(data.get(0).toArray(), false, parameterTypes);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
    }

    @Test
    public void testConvertShouldReturnMultipleElementsForListOfListOfObjectWithMultipleElements() {
        // Given:
        @SuppressWarnings("unchecked")
        List<List<?>> data = list(this.<Object> list('x', "foo"), list('y', "bar"), list('z', "baz"));
        Class<?>[] parameterTypes = new Class<?>[] { char.class, String.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(3);
        InOrder inOrder = inOrder(objectArrayConverter, singleArgConverter, stringConverter);
        inOrder.verify(objectArrayConverter).convert(data.get(0).toArray(), false, parameterTypes);
        inOrder.verify(objectArrayConverter).convert(data.get(1).toArray(), false, parameterTypes);
        inOrder.verify(objectArrayConverter).convert(data.get(2).toArray(), false, parameterTypes);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
    }

    @Test
    public void testConvertShouldCallSingleArgConverterOnlyOnceForObjectArrayWithSingleElement() {
        // Given:
        Object[] data = new Object[] { 88.99 };
        Class<?>[] parameterTypes = new Class<?>[] { Object.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(1);
        verify(singleArgConverter).convert(data[0], false, parameterTypes);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
    }

    @Test
    public void testConvertShouldCallSingleArgConverterMultipleTimesForObjectArrayWithMultipleElements() {
        // Given:
        Object[] data = new Object[] { "12", 34L, 5.6 };
        Class<?>[] parameterTypes = new Class<?>[] { Object.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(3);
        InOrder inOrder = inOrder(objectArrayConverter, singleArgConverter, stringConverter);
        inOrder.verify(singleArgConverter).convert(data[0], false, parameterTypes);
        inOrder.verify(singleArgConverter).convert(data[1], false, parameterTypes);
        inOrder.verify(singleArgConverter).convert(data[2], false, parameterTypes);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
    }

    @Test
    public void testConvertShouldCallSingleArgConverterOnlyOnceForListOfObjectWithSingleElement() {
        // Given:
        List<Object> data = this.<Object> list(88);
        Class<?>[] parameterTypes = new Class<?>[] { Object.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(1);
        verify(singleArgConverter).convert(data.get(0), false, parameterTypes);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
        // TODO only false for varargs?
    }

    @Test
    public void testConvertShouldCallSingleArgConverterMultipleTimesForListOfObjectWithMultipleElements() {
        // Given:
        List<Object> data = this.<Object> list("12", 34L, 5.6);
        Class<?>[] parameterTypes = new Class<?>[] { Object.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(3);
        InOrder inOrder = inOrder(objectArrayConverter, singleArgConverter, stringConverter);
        inOrder.verify(singleArgConverter).convert(data.get(0), false, parameterTypes);
        inOrder.verify(singleArgConverter).convert(data.get(1), false, parameterTypes);
        inOrder.verify(singleArgConverter).convert(data.get(2), false, parameterTypes);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
        // TODO only false for varargs?
    }

    @Test
    public void testConvertShouldCallStringConverterOnlyOnceForStringArrayWithOneElement() {
        // Given:
        String[] data = new String[] { "foo,true" };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, boolean.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(1);
        verify(stringConverter).convert(data[0], false, parameterTypes, dataProvider, 0);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
    }

    @Test
    public void testConvertCallStringConverterMultipleTimesForStringArrayWithMultipleElements() {
        // Given:
        String[] data = new String[] { "1, 2, 3, 4.0, e", "6, 7, 8, 9.0, i" };
        Class<?>[] parameterTypes = new Class<?>[] { byte.class, int.class, long.class, double.class, char.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(2);
        InOrder inOrder = inOrder(objectArrayConverter, singleArgConverter, stringConverter);
        inOrder.verify(stringConverter).convert(data[0], false, parameterTypes, dataProvider, 0);
        inOrder.verify(stringConverter).convert(data[1], false, parameterTypes, dataProvider, 1);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
    }

    // -- methods used as Method objects -------------------------------------------------------------------------------

    public static List<List<Object>> methodReturningListOfListOfObject() {
        return null;
    }

    public static List<List<?>> methodReturningListOfListOfWildcard() {
        return null;
    }

    public static List<Iterable<Number>> methodReturningListOfIterableOfNumber() {
        return null;
    }

    public static <T extends CharSequence> Iterable<Iterable<T>> methodReturningIterableOfIterableOfT() {
        return null;
    }

    public static Set<Set<?>> methodReturningSetOfSetOfWildcard() {
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

    public static Object[] methodReturningObjectArray() {
        return null;
    }

    public static String[] methodReturningStringArray() {
        return null;
    }

    public static List<Object> methodReturningListOfObject() {
        return null;
    }

    public static List<?> methodReturningIterableOfWildcard() {
        return null;
    }

    public static List<?> methodReturningListOfWildcard() {
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
