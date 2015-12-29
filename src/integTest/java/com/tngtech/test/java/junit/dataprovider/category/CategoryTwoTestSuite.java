package com.tngtech.test.java.junit.dataprovider.category;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Categories.class)
@SuiteClasses(CategoryTest.class)
@IncludeCategory(CategoryTwo.class)
public class CategoryTwoTestSuite {
    // suite to run tests with
}
