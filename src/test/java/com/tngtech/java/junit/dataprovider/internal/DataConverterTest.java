package com.tngtech.java.junit.dataprovider.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.tngtech.java.junit.dataprovider.BaseTest;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.internal.convert.ObjectArrayConverter;
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
    public void testCanConvertShouldReturnTrueIfTypeIsListListObject() {
        // Given:
        Type type = getMethod("methodReturningListOfListOfObject").getGenericReturnType();

        // When:
        boolean result = underTest.canConvert(type);

        // Then:
        assertThat(result).isTrue();
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
    public void testConvertShouldReturnOneElementForObjectArrayArrayWithOneElement() {
        // Given:
        Object[][] data = new Object[][] { { 1 } };
        Class<?>[] parameterTypes = new Class<?>[] { int.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).containsExactly(data[0]);
    }

    @Test
    public void testConvertShouldReturnMultipleElementsForObjectArrayArrayWithMultipleElements() {
        // Given:
        Object[][] data = new Object[][] { { "11", 22L, 3.3 }, { "44", 55L, 6.6 }, { "77", 88L, 9.9 } };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, long.class, double.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).containsExactly(data[0], data[1], data[2]);
    }

    @Test
    public void testConvertShouldReturnMultipleElementsForObjectArrayWithMultipleElements() {
        // Given:
        Object[] data = new Object[] { "12", 34L, 5.6 };
        Class<?>[] parameterTypes = new Class<?>[] { Object.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).containsExactly(new Object[] { "12" }, new Object[] { 34L }, new Object[] { 5.6 });
    }

    @Test
    public void testConvertShouldReturnOneElementForListOfListOfObjectWithOneElement() {
        // Given:
        @SuppressWarnings("unchecked")
        List<List<Character>> data = list(list('a'));
        Class<?>[] parameterTypes = new Class<?>[] { char.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).containsExactly(data.get(0).toArray());
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
        assertThat(result).containsExactly(data.get(0).toArray(), data.get(1).toArray(), data.get(2).toArray());
    }

    @Test
    public void testConvertShouldReturnMultipleElementsForListOfObjectWithMultipleElements() {
        // Given:
        List<Object> data = this.<Object> list("12", 34L, 5.6);
        Class<?>[] parameterTypes = new Class<?>[] { Object.class };

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).containsExactly(new Object[] { "12" }, new Object[] { 34L }, new Object[] { 5.6 });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertShouldThrowIllegalArgumentExceptionIfLengthOfSplitDataAndTargetTypesDiffer() {
        // Given:
        String[] data = new String[] { "1,2" };
        Class<?>[] parameterTypes = new Class[] { int.class };

        doReturn(",").when(dataProvider).splitBy();

        // When:
        underTest.convert(data, false, parameterTypes, dataProvider);

        // Then: expect exception
    }

    @Test
    public void testConvertShouldReturnOneElementForStringArrayWithOneElementSplitByComma() {
        // Given:
        String[] data = new String[] { "foo,true" };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, boolean.class };

        doReturn(",").when(dataProvider).splitBy();

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).containsExactly(new Object[] { "foo", true });
    }

    @Test
    public void testConvertShouldReturnOneElementForStringArrayWithOneElementSplitByPipe() {
        // Given:
        String[] data = new String[] { "bar|false" };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, boolean.class };

        doReturn("\\|").when(dataProvider).splitBy();

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).containsExactly(new Object[] { "bar", false });
    }

    @Test
    public void testConvertShouldReturnOneElementForStringArrayWithOneElementSplitByMultipleWhitespaces() {
        // Given:
        String[] data = new String[] { "baz    2" };
        Class<?>[] parameterTypes = new Class<?>[] { String.class, int.class };

        doReturn("\\s+").when(dataProvider).splitBy();

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).containsExactly(new Object[] { "baz", 2 });
    }

    @Test
    public void testConvertShouldReturnMultipleElementsForStringArrayWithMultipleElements() {
        // Given:
        String[] data = new String[] { "1, 2, 3, 4.0, e", "6, 7, 8, 9.0, i" };
        Class<?>[] parameterTypes = new Class<?>[] { byte.class, int.class, long.class, double.class, char.class };

        doReturn(",").when(dataProvider).splitBy();
        doReturn(true).when(dataProvider).trimValues();

        // When:
        List<Object[]> result = underTest.convert(data, false, parameterTypes, dataProvider);

        // Then:
        assertThat(result).containsExactly(new Object[] { (byte) 1, 2, 3l, 4.0, 'e' }, new Object[] { (byte) 6, 7, 8l, 9.0, 'i' });
    }

    // -- methods used as Method objects -------------------------------------------------------------------------------

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

    public static Object[] methodReturningObjectArray() {
        return null;
    }

    public static String[] methodReturningStringArray() {
        return null;
    }

    public static List<List<Object>> methodReturningListOfListOfObject() {
        return null;
    }

    public static List<Object> methodReturningListOfObject() {
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
