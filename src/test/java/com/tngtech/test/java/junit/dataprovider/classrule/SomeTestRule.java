package com.tngtech.test.java.junit.dataprovider.classrule;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class SomeTestRule extends TestWatcher {
    boolean started;

    @Override
    protected void starting( Description description ) {
        started = true;
    }

    @Override
    protected void finished( Description description ) {
        assertThat( started ).as( "Rule was not started, but finished called" ).isTrue();
    }

}
