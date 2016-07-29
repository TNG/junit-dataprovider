package com.tngtech.test.java.junit.dataprovider.spring;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.test.java.junit.dataprovider.spring.DataProviderSpringAcceptanceTest.TestConfiguration;

@RunWith(SpringDataProviderRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class DataProviderSpringAcceptanceTest {

    @Autowired
    private Greeter greeter;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("beforeClass");
    }

    @Before
    public void before() {
        System.out.println("before");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("after");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        System.out.println("afterClass");
    }

    @DataProvider
    public static Object[][] dataProviderCreateGreeting() {
        //@formatter:off
        return new Object[][] {
            { "Spring" },
            { "JUnit dataprovider" },
        };
        //@formatter:on
    }

    @Test
    @UseDataProvider(value = "dataProviderCreateGreeting")
    public void testCreateGreeting(String name) throws Exception {
        System.out.println("test");

        // When:
        String result = greeter.createGreetingFor(name);

        // Then:
        assertThat(result).containsSequence("Hello", name, "!");
    }

    @Configuration
    public static class TestConfiguration {
        @Bean
        public Greeter greeter() {
            return new Greeter();
        }
    }
}
