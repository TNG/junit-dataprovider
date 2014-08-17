package com.tngtech.test.java.junit.dataprovider.classrule;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith( DataProviderRunner.class )
public class ClassRuleAcceptanceTest {

    @ClassRule
    public static SomeTestRule testClassRule = new SomeTestRule();

    @Rule
    public SomeTestRule someTestRule = new SomeTestRule();

    @Test
    public void classRulesShouldBeStartedBeforeTestMethods() {
        assertThat( testClassRule.started ).as( "ClassRule was not started" ).isTrue();
    }

    @Test
    public void testRulesShouldBeStartedBeforeTestMethods() {
        assertThat( someTestRule.started ).as( "TestRule was not started" ).isTrue();
    }

}
