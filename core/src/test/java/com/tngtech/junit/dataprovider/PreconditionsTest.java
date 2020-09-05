package com.tngtech.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.tngtech.junit.dataprovider.Preconditions;

public class PreconditionsTest {

    @SuppressWarnings("deprecation")
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @SuppressFBWarnings(value = "NP_NONNULL_PARAM_VIOLATION", justification = "Test that it works properly if null ;-)")
    @Test
    public void testCheckNotNullShouldThrowNullPointerExceptionWithGivenMessageStringIfObjectIsNull() {
        // Given:
        String errorMessage = "error message";

        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(errorMessage);

        // When:
        Preconditions.checkNotNull(null, errorMessage);

        // Then: expect exception
    }

    @Test
    public void testCheckNotNullShouldNotThrowNullPointerExceptionAndReturnObjectIfObjectIsNull() {
        // Given:
        Object object = new Object();

        // When:
        Object result = Preconditions.checkNotNull(object, "error message");

        // Then:
        assertThat(result).isSameAs(object);
    }

    @Test
    public void testCheckArgumentShouldThrowIllegalArgumentExceptionIfExpressionIsFalse() {
        // Given:
        String errorMessage = "error message";

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(errorMessage);

        // When:
        Preconditions.checkArgument(1 == 2, errorMessage);

        // Then: expect exception
    }

    @Test
    public void testCheckArgumentShouldNotThrowIllegalArgumentExceptionIfExpressionIsTrue() {
        // When:
        Preconditions.checkArgument(true, "error message");

        // Then: expect no exception
    }

    @Test
    public void testCheckArgumentShouldThrowIllegalArgumentExceptionIfExpressionIsFalseAndReturnFormattedMessage() {
        // Given:
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("error message template");

        // When:
        Preconditions.checkArgument(1 == 2, "error message %s", "template");

        // Then: expect exception
    }

    @Test
    public void testCheckArgumentShouldNotThrowIllegalArgumentExceptionIfExpressionIsTrueUsingMessageTemplate() {
        // When:
        Preconditions.checkArgument(true, "error message %s", "template");

        // Then: expect no exception
    }

    @Test
    public void testCheckStateShouldThrowIllegalStateExceptionIfExpressionIsFalse() {
        // Given:
        String errorMessage = "error message";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(errorMessage);

        // When:
        Preconditions.checkState(1 == 2, errorMessage);

        // Then: expect exception
    }

    @Test
    public void testCheckStateShouldNotThrowIllegalStateExceptionIfExpressionIsTrue() {
        // When:
        Preconditions.checkState(true, "error message");

        // Then: expect no exception
    }

    @Test
    public void testCheckStateShouldThrowIllegalStateExceptionIfExpressionIsFalseAndReturnFormattedMessage() {
        // Given:
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("error message template");

        // When:
        Preconditions.checkState(1 == 2, "error message %s", "template");

        // Then: expect exception
    }

    @Test
    public void testCheckStateShouldNotThrowIllegalStateExceptionIfExpressionIsTrueUsingMessageTemplate() {
        // When:
        Preconditions.checkState(true, "error message %s", "template");

        // Then: expect no exception
    }
}
