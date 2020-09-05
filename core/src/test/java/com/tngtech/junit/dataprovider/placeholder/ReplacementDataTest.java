package com.tngtech.junit.dataprovider.placeholder;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.tngtech.junit.dataprovider.testutils.Methods;

public class ReplacementDataTest {

    @SuppressWarnings("deprecation")
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testOfShouldThrowNullPointerExceptionIfTestMethodIsNull() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'testMethod' must not be null");

        // When:
        ReplacementData.of(null, 0, new ArrayList<Object>());

        // Then: expect exception
    }

    @Test
    public void testOfShouldThrowNullPointerExceptionIfArgumentsAreNull() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'arguments' must not be null");

        // When:
        ReplacementData.of(Methods.anyMethod(), 1, null);

        // Then: expect exception
    }

    @Test
    public void testOfShouldCreateReplacementDataProperly() {
        // Given:
        final Method testMethod = Methods.anyMethod();
        final int testIndex = 10;
        final List<Object> arguments = Arrays.<Object>asList("1", 2, 3L);

        // When:
        ReplacementData result = ReplacementData.of(testMethod, testIndex, arguments);

        // Then:
        assertThat(result).isNotNull();
        assertThat(result.getTestMethod()).isEqualTo(testMethod);
        assertThat(result.getTestIndex()).isEqualTo(testIndex);
        assertThat(result.getArguments()).isNotSameAs(arguments).isEqualTo(arguments);
    }

    @Test
    public void testGetArgumentsShouldReturnUnmodifiableListToKeepReplacementDataImmutable() {
        // Given:
        ReplacementData underTest = ReplacementData.of(Methods.anyMethod(), 11, Arrays.<Object>asList("1", 2, 3L));
        List<Object> arguments = underTest.getArguments();

        expectedException.expect(UnsupportedOperationException.class);

        // When:
        arguments.clear();

        // Then: expect exception
    }

    @Test
    public void testHashCodeShouldReturnSameResultForEqualObjects() {
        // Given:
        final Method testMethod = Methods.anyMethod();
        final int testIndex = 20;
        final List<Object> arguments = Arrays.<Object>asList("1", 2, 3L);

        ReplacementData data1 = ReplacementData.of(testMethod, testIndex, arguments);
        ReplacementData data2 = ReplacementData.of(testMethod, testIndex, arguments);

        // When:
        int result1 = data1.hashCode();
        int result2 = data2.hashCode();

        // Then:
        assertThat(result1).isEqualTo(result2);
    }

    @Test
    public void testEqualsShouldReturnFalseForUnequalObjects() {
        // Given:
        final Method testMethod = Methods.anyMethod();
        final List<Object> arguments = Arrays.<Object>asList("1", 2, 3L);

        ReplacementData data1 = ReplacementData.of(testMethod, 30, arguments);
        ReplacementData data2 = ReplacementData.of(testMethod, 31, arguments);

        // When:
        boolean result = data1.equals(data2);

        // Then:
        assertThat(result).isFalse();
    }

    @Test
    public void testEqualsShouldReturnTrueForEqualObjects() {
        // Given:
        final Method testMethod = Methods.anyMethod();
        final int testIndex = 32;
        final List<Object> arguments = Arrays.<Object>asList("1", 2, 3L);

        ReplacementData data1 = ReplacementData.of(testMethod, testIndex, arguments);
        ReplacementData data2 = ReplacementData.of(testMethod, testIndex, arguments);

        // When:
        boolean result = data1.equals(data2);

        // Then:
        assertThat(result).isTrue();
    }
}
