package com.tngtech.test.java.junit.dataprovider.spring;

public class Greeter {

    public String createGreetingFor(String name) {
        return String.format("Hello %s!", name);
    }
}
