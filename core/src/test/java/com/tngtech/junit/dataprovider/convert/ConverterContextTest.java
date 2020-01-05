package com.tngtech.junit.dataprovider.convert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConverterContextTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testConverterContextShouldThrowNullPointerExceptionIfObjectArrayConverterIsNull() {
        // Given
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'objectArrayConverter' must not be null");

        // When:
        new ConverterContext(null, new SingleArgConverter(), new StringConverter(), "\\|", false, false, false);

        // Then: expect exception
    }

    @Test
    public void testConverterContextShouldThrowNullPointerExceptionIfSingleArgConverterIsNull() {
        // Given
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'singleArgConverter' must not be null");

        // When:
        new ConverterContext(new ObjectArrayConverter(), null, new StringConverter(), "\\|", false, false, false);

        // Then: expect exception
    }

    @Test
    public void testConverterContextShouldThrowNullPointerExceptionIfStringConverterIsNull() {
        // Given
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'stringConverter' must not be null");

        // When:
        new ConverterContext(new ObjectArrayConverter(), new SingleArgConverter(), null, "\\|", false, false, false);

        // Then: expect exception
    }

    @Test
    public void testConverterContextShouldThrowNullPointerExceptionIfSplitByIsNull() {
        // Given
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("'splitBy' must not be null");

        // When:
        new ConverterContext(null, false, false, false);

        // Then: expect exception
    }
}
