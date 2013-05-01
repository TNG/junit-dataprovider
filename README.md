junit-dataprovider
==================

#### Table of Contents  
[What is it](#What is it)  
[Motivation and distinction](#Motivation and distinction)  
[Requirements](#Requirements)  
[Download](#Download)  
[Usage example](#Usage example)  
[Eclipse template](#Eclipse template)  


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

> Test cases for [JUnit Theories][] are built from all data points whose type matches 
> the method's argument – or even the cross product of all matching data points, 
> if the method takes several arguments. The junit-dataprovider, however, ddresses 
> another use case: Its test cases may consist of multiple parameters that belong together, 
> which may contain test input values and/or expected values to assert the result.
> Furthermore, a test method using [JUnit Theories][] fails or succeeds entirely (for alle 
> data points), on the contrary the junit-dataprovider considers each row of the data provider
> as standalone test case.


#### Why can I not use [JUnit Theories][] and data points containing [DTO][]s for test cases?

> Of course, this is also a possible way to use [JUnit Theories][], constructing DTOs for 
> every single data point causes a lot of [boiler plate](http://en.wikipedia.org/wiki/Boilerplate_%28text%29)
> code and inconvenience. This is AFAIK also the case when you use the ParameterSupplier 
> feature of [JUnit Theories][], where you additionally need a custom Annotation and a class...

[DTO]: http://en.wikipedia.org/wiki/Data_transfer_object


#### But why does [JUnit][] not support data providers?

> They do, having another name for it, tough, just see [Parameterized][]. 
> The advantage of this concept is surely that it is completely 
> [typesafe](http://en.wikipedia.org/wiki/Type_safety). But unfortunatly one has to create
> a class per data provider or parameterized test, respectively, which is IMHO also overkill.
> The tests of a single unit (i.e. class) have to be divided into different classes, 
> which need to be maintained (renamed, moved etc.) separately.
> Furthermore, [Parameterized][] tests (even if there are more than a single test method within 
> one test class) can only be executed altoghter for a test class. A junit dataprovider test, though, 
> can be executed independent on test method level (even if the same data provider is reused for 
> more than one test method).

[Parameterized]: https://github.com/junit-team/junit/wiki/Parameterized-tests


#### Is it possible to execute a junit-dataprovider test method for a single test data row?

> Unfortunately this is not possible directly expect if the other test data rows are uncommented 
> in the source code. The rerun of a single test data row is working, though, if e.g. in Eclipse 
> you right click the test to be executed and choose run/debug.


[JUnit Theories]: https://github.com/junit-team/junit/wiki/Theories


Requirements
-----------

This JUnit dataprovider requires JUnit in version 4.8.2+ (see 
[junit-dep-4.8.2](http://search.maven.org/#artifactdetails|junit|junit-dep|4.8.2|jar)
/ [junit-4.8.2](http://search.maven.org/#artifactdetails|junit|junit|4.8.2|jar)). 

If you are using a previous version and cannot upgrade, please let us know by opening an issue.

Download
--------

All released (= tagged) versions are available at 
[Maven Central Repository](http://search.maven.org/#search|ga|1|a%3A%22junit-dataprovider%22). 
Following this link you can choose a version. Now either download it manually or see 
the **Dependency Information** section how to integrate it with your dependency management tool.


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

Eclipse template
----------------

* Name:                     dataProvider
* Context:                  Java type members
* Automatically insert:     false
* Description:              Insert a junit dataprovider method
* Pattern:

```
@${dataProviderType:newType(com.tngtech.java.junit.dataprovider.DataProvider)}
public static Object[][] dataProvider${Name}() {
    // @formatter:off
	return new Object[][] {
		{ ${cursor} },
	};
	// @formatter:on
}
```
