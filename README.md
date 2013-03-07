junit-dataprovider
==================

What is it
----------

A TestNG like dataprovider runner for JUnit.

Reqirements
-----------

This JUnit dataprovider requires JUnit in version 4.11+. If you are using a former version, please let us know opening an issue.

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
