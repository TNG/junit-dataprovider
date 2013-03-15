junit-dataprovider
==================

#### Table of Contents  
[What is it](#What is it)  
[Motivation and distinction](#Motivation and distinction)  
[Requirements](#Requirements)  
[Download](#Download)  
[Usage example](#Usage example)  


What is it
----------

A [TestNG](http://testng.org/doc/index.html) like 
dataprovider (see [here](http://testng.org/doc/documentation-main.html#parameters-dataproviders)) 
runner for [JUnit][] having a simplified syntax 
compared to all the existing [JUnit features](https://github.com/junit-team/junit/wiki).

[JUnit]: https://github.com/junit-team/junit

Motivation and distinction 
--------------------------

#### What is the advantage compared to [JUnit Theories][]?

> The junit-dataprovider is made for another use case. Using 
> [JUnit Theories](https://github.com/junit-team/junit/wiki/Theories) the test cases 
> where build for each data point of the matching Java type or even the cross product 
> of each for multiple data point parameters.
> The junit-dataprovider, however, wants a defined input for each test case using multiple 
> parameters and one or more expected values to assert the result. 

#### Why can I not use [JUnit Theories][] and data points containing [DTO][]s for test case?

> Of course, this is also a possible way to get it done using [JUnit Theories][]
> but this causes a lot [boiler plate](http://en.wikipedia.org/wiki/Boilerplate_%28text%29) 
> code and inconvenience because it requires a DTO for every single data point. This is AFAIK
> also the case when you use the ParameterSupplier feature of [JUnit Theories][], 
> where you additionally need a custom Annotation and a class...

[DTO]: http://en.wikipedia.org/wiki/Data_transfer_object

#### But why [JUnit][] does not support data providers?

> They do having another name it, tough, just see 
> [Parameterized](https://github.com/junit-team/junit/wiki/Parameterized-tests). 
> The advantage of this concept is surely that it is completely 
> [typesafe](http://en.wikipedia.org/wiki/Type_safety). Unfortunatly one have to create
> a class per data provider or parameterized test, respectively, which is IMHO also overkill.
> Furthermore the tests of a single unit (= class) have to divided into different classes 
> which need to be maintained separately (renamed/moved etc.).

[JUnit Theories]: https://github.com/junit-team/junit/wiki/Theories

Requirements
-----------

This JUnit dataprovider requires JUnit in version 4.8.2+ (see 
[junit-deb-4.8.2](http://search.maven.org/#artifactdetails|junit|junit-dep|4.8.2|jar)
/ [junit-4.8.2](http://search.maven.org/#artifactdetails|junit|junit|4.8.2|jar)). 

If you are using a previous version and cannot upgrade, please let us know opening an issue.

Download
--------

All released (= tagged) versions are available at 
[Maven Central Repository](http://search.maven.org/#search|ga|1|a%3A%22junit-dataprovider%22). 
Following this link you can choose a version. Now either download it manually or see 
the **Dependency Information** section howto integrate it with your dependency managenment tool.


Usage example
-------------

```java
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderTest {

    @DataProvider
    public static Object[][] dataProviderAdd() {
        // @formatter:off
        return new Object[][] {
                { 0, 0, 0 },
                { 1, 1, 2 },
                /* ... */
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderAdd")
    public void testAdd(int a, int b, int expected) {
        // Given:

        // When:
        int result = a + b;

        // Then:
        assertEquals(expected, result);
    }
}
```
