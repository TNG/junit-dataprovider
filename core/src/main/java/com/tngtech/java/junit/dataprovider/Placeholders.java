package com.tngtech.java.junit.dataprovider;

import java.util.ArrayList;
import java.util.List;

import com.tngtech.java.junit.dataprovider.internal.placeholder.BasePlaceholder;
import com.tngtech.java.junit.dataprovider.internal.placeholder.CanonicalClassNamePlaceholder;
import com.tngtech.java.junit.dataprovider.internal.placeholder.CompleteMethodSignaturePlaceholder;
import com.tngtech.java.junit.dataprovider.internal.placeholder.IndexPlaceholder;
import com.tngtech.java.junit.dataprovider.internal.placeholder.ParameterPlaceholder;
import com.tngtech.java.junit.dataprovider.internal.placeholder.SimpleClassNamePlaceholder;
import com.tngtech.java.junit.dataprovider.internal.placeholder.SimpleMethodNamePlaceholder;

/**
 * Use this class to manipulate the generation/formatting of test method names.
 * <p>
 * E.g. one can add a new placeholder using a static initializer block in a base class of all tests:
 *
 * <pre>
 * <code>
 * public static class BaseTest {
 *     static {
 *         Placeholders.all().add(0, new MyFancyParameterPlaceholder());
 *     }
 *     // ...
 * }
 * </code>
 * </pre>
 */
public class Placeholders {

    private static final List<BasePlaceholder> placeholders = new ArrayList<BasePlaceholder>();
    static {
        reset();
    }

    /**
     * Retrieve all {@link BasePlaceholder} to handle {@link DataProvider#format()}. The returned {@link List} is the
     * original list such that all manipulations will change the behavior how test method names are formatted.
     * <p>
     * <b>Note:</b>
     * <ul>
     * <li>The placeholder are process in order.</li>
     * <li>The first matching placeholder wins, especially if an earlier processed placeholder is a substring of a later
     * one (e.g. {@code %c} and {@code %cc})</li>
     * </ul>
     *
     * @return all {@link BasePlaceholder}s to handle {@link DataProvider#format()} (not a copy!)
     */
    public static List<BasePlaceholder> all() {
        return placeholders;
    }

    /**
     * Resets all changes to the list of all {@link BasePlaceholder} such that is contains the default placeholders
     * again.
     */
    public static void reset() {
        placeholders.clear();
        placeholders.add(new CanonicalClassNamePlaceholder());
        placeholders.add(new CompleteMethodSignaturePlaceholder());
        placeholders.add(new IndexPlaceholder());
        placeholders.add(new ParameterPlaceholder());
        placeholders.add(new SimpleClassNamePlaceholder());
        placeholders.add(new SimpleMethodNamePlaceholder());
    }
}
