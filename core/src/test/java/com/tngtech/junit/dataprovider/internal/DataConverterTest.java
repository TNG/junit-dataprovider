package com.tngtech.junit.dataprovider.internal;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.internal.convert.ObjectArrayConverter;
import com.tngtech.junit.dataprovider.internal.convert.SingleArgConverter;
import com.tngtech.junit.dataprovider.internal.convert.StringConverter;

public class DataConverterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

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
        Type type = parameterizedType(List.class, Object.class);

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsIterableOfWildcard() {
        // Given:
        Type type = parameterizedType(Iterable.class, WildcardType.class);

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsListOfWildcard() {
        // Given:
        Type type = parameterizedType(List.class, WildcardType.class);

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsIterableOfIterable() {
        // Given:
        Type type = parameterizedType(Iterable.class, rawType(Iterable.class));
        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsSetOfSet() {
        // Given:
        Type type = parameterizedType(Set.class, rawType(Set.class));

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnFalseIfTypeIsTwoArgList() {
        // Given:
        @SuppressWarnings({ "serial", "unused" })
        class TwoArgList<A, B> extends ArrayList<A> {
            // not required for now :-)
        }

        Type type = parameterizedType(TwoArgList.class, mock(ParameterizedType.class), mock(ParameterizedType.class));

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsObjectArrayArray() {
        // Given:
        Type type = Object[][].class;

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsObjectArray() {
        // Given:
        Type type = Object[].class;

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsStringArray() {
        // Given:
        Type type = String[].class;

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsListOfListOfObject() {
        // Given:
        Type type = parameterizedType(List.class, rawType(List.class));

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsListOfListOfWildcard() {
        // Given:
        Type type = parameterizedType(List.class, rawType(List.class));

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsListOfIterable() {
        // Given:
        Type type = parameterizedType(List.class, rawType(Iterable.class));

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testCanConvertShouldReturnTrueIfTypeIsSubListSubListString() {
        // Given:
        @SuppressWarnings("serial")
        class SubList<A> extends ArrayList<A> {
            // not required for now :-)
        }
        Type type = parameterizedType(SubList.class, rawType(SubList.class));

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
    }

    @Test
    public void testConvertShouldThrowNullPointerExceptionIfParameterTypesIsNull() {
        // Given:
        Object data = null;
        Class<?>[] parameterTypes = null;

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'parameterTypes' must not be null");

        // When:
        underTest.convert(data, false, parameterTypes, dataProvider);

        // Then: expect exception
    }

    @Test
    public void testConvertShouldThrowNullPointerExceptionIfDataProviderIsNull() {
        // Given:
        Object data = null;
        Class<?>[] parameterTypes = new Class<?>[] { Object.class };

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'dataProvider' must not be null");

        // When:
        underTest.convert(data, false, parameterTypes, null);

        // Then: expect exception
    }

    @Test
    public void testConvertShouldThrowIllegalArgumentExceptionIfParameterTypesIsEmpty() {
        // Given:
        Object data = null;
        Class<?>[] parameterTypes = new Class<?>[0];

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'parameterTypes' must not be empty");

        // When:
        underTest.convert(data, false, parameterTypes, dataProvider);

        // Then: expect exception
    }

    @Test
    public void testConvertShouldThrowClassCastExceptionIfDataIsNull() {
        // Given:
        Object data = null;
        Class<?>[] parameterTypes = new Class<?>[] { String.class };

        expectedException.expect(ClassCastException.class);
        expectedException.expectMessage(
                "Cannot cast to either Object[][], Object[], String[], or Iterable because data was: null");

        // When:
        underTest.convert(data, false, parameterTypes, dataProvider);

        // Then: expect exception
    }

    @Test
    public void testConvertShouldThrowClassCastExceptionIfDataIsNotConvertable() {
        // Given:
        Object data = "not convertable";
        Class<?>[] parameterTypes = new Class<?>[] { Integer.class };

        expectedException.expect(ClassCastException.class);
        expectedException.expectMessage(
                "Cannot cast to either Object[][], Object[], String[], or Iterable because data was: " + data);

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
        List<List<Character>> data = asList(asList('a'));
        Class<?>[] parameterTypes = new Class<?>[] { char.class };

        // When:
        List<Object[]> result = underTest.convert(data, true, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(1);
        verify(objectArrayConverter).convert(data.get(0).toArray(), true, parameterTypes);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
    }

    @Test
    public void testConvertShouldReturnMultipleElementsForListOfListOfObjectWithMultipleElements() {
        // Given:
        @SuppressWarnings("unchecked")
        List<List<?>> data = asList(Arrays.<Object>asList('x', "foo"), asList('y', "bar"), asList('z', "baz"));
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
        List<Object[]> result = underTest.convert(data, true, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(1);
        verify(singleArgConverter).convert(data[0], true, parameterTypes);
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
        List<Object> data = Arrays.<Object>asList(88);
        Class<?>[] parameterTypes = new Class<?>[] { Object.class };

        // When:
        List<Object[]> result = underTest.convert(data, true, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(1);
        verify(singleArgConverter).convert(data.get(0), true, parameterTypes);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
    }

    @Test
    public void testConvertShouldCallSingleArgConverterMultipleTimesForListOfObjectWithMultipleElements() {
        // Given:
        List<Object> data = Arrays.<Object>asList("12", 34L, 5.6);
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
    }

    @Test
    public void testConvertShouldCallStringConverterOnlyOnceForStringArrayWithOneElement() {
        // Given:
        String[] data = new String[] { "foo,true" };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, boolean.class };

        // When:
        List<Object[]> result = underTest.convert(data, true, parameterTypes, dataProvider);

        // Then:
        assertThat(result).hasSize(1);
        verify(stringConverter).convert(data[0], true, parameterTypes, dataProvider, 0);
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

    private Type rawType(Type rawType) {
        ParameterizedType type = mock(ParameterizedType.class);
        when(type.getRawType()).thenReturn(rawType);
        return type;
    }

    private Type parameterizedType(Type rawType, Type... typeArguments) {
        ParameterizedType type = mock(ParameterizedType.class);
        when(type.getRawType()).thenReturn(rawType);
        when(type.getActualTypeArguments()).thenReturn(typeArguments);
        return type;
    }
}
