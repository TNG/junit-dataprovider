package com.tngtech.test.java.junit.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith(DataProviderRunner.class)
public class RuleAcceptanceTest {

    public static class SomeTestRule extends TestWatcher {
        boolean started;

        @Override
        protected void starting(Description description) {
            started = true;
        }

        @Override
        protected void finished(Description description) {
            assertThat(started).as("Rule was not started, but 'finished' called").isTrue();
        }
    }

    @ClassRule
    public static final SomeTestRule classRule = new SomeTestRule();

    @Rule
    public SomeTestRule rule = new SomeTestRule();

    @Test
    public void testClassRuleShouldBeStartedBeforeTest() {
        // Expected:
        assertThat(classRule.started).as("'@ClassRule' was not started").isTrue();
    }

    @Test
    public void testRuleShouldBeStartedBeforeTest() {
        // Expected:
        assertThat(rule.started).as("'@Rule' was not started").isTrue();
    }
}
