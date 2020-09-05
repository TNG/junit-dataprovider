package com.tngtech.junit.dataprovider.convert;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class DataConverterTest {

    @SuppressWarnings("deprecation")
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification = "Mockito rule needs no further configuration")
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    private DataConverter underTest;

    private ConverterContext context;

    @Mock
    private ObjectArrayConverter objectArrayConverter;

    @Mock
    private SingleArgConverter singleArgConverter;

    @Mock
    private StringConverter stringConverter;

    @Before
    public void setup() {
        context = new ConverterContext(objectArrayConverter, singleArgConverter, stringConverter, "\\|", true, true,
                false);
    }

    @Test
    public void testConvertShouldThrowNullPointerExceptionIfParameterTypesIsNull() {
        // Given:
        Object data = null;
        Class<?>[] parameterTypes = null;

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'parameterTypes' must not be null");

        // When:
        underTest.convert(data, false, parameterTypes, context);

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
        underTest.convert(data, false, parameterTypes, context);

        // Then: expect exception
    }

    @Test
    public void testConvertShouldThrowIllegalStateExceptionIfConverterContextIsNullForString() {
        // Given:
        String[] data = { "" };
        Class<?>[] parameterTypes = new Class<?>[] { Object.class };

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("'context' must not be null for 'String[]' data");

        // When:
        underTest.convert(data, false, parameterTypes, null);

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
        underTest.convert(data, false, parameterTypes, context);

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
        underTest.convert(data, false, parameterTypes, context);

        // Then: expect exception
    }

    @Test
    public void testConvertShouldCallObjectArrayConverterOnlyOnceForObjectArrayArrayWithOneElement() {
        // Given:
        Object[][] data = new Object[][] { { 1 } };
        Class<?>[] parameterTypes = new Class<?>[] { int.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, context);

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
        List<Object[]> result = underTest.convert(data, false, parameterTypes, context);

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
        List<List<Character>> data = singletonList(singletonList('a'));
        Class<?>[] parameterTypes = new Class<?>[] { char.class };

        // When:
        List<Object[]> result = underTest.convert(data, true, parameterTypes, context);

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
        List<Object[]> result = underTest.convert(data, false, parameterTypes, context);

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
        List<Object[]> result = underTest.convert(data, true, parameterTypes, context);

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
        List<Object[]> result = underTest.convert(data, false, parameterTypes, context);

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
        List<Object> data = Collections.<Object>singletonList(88);
        Class<?>[] parameterTypes = new Class<?>[] { Object.class };

        // When:
        List<Object[]> result = underTest.convert(data, true, parameterTypes, context);

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
        List<Object[]> result = underTest.convert(data, false, parameterTypes, context);

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
        List<Object[]> result = underTest.convert(data, true, parameterTypes, context);

        // Then:
        assertThat(result).hasSize(1);
        verify(stringConverter).convert(data[0], true, parameterTypes, context, 0);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
    }

    @Test
    public void testConvertCallStringConverterMultipleTimesForStringArrayWithMultipleElements() {
        // Given:
        String[] data = new String[] { "1, 2, 3, 4.0, e", "6, 7, 8, 9.0, i" };
        Class<?>[] parameterTypes = new Class<?>[] { byte.class, int.class, long.class, double.class, char.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, context);

        // Then:
        assertThat(result).hasSize(2);
        InOrder inOrder = inOrder(objectArrayConverter, singleArgConverter, stringConverter);
        inOrder.verify(stringConverter).convert(data[0], false, parameterTypes, context, 0);
        inOrder.verify(stringConverter).convert(data[1], false, parameterTypes, context, 1);
        verifyNoMoreInteractions(objectArrayConverter, singleArgConverter, stringConverter);
    }
}
