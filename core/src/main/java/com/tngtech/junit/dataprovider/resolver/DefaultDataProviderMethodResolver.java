package com.tngtech.junit.dataprovider.resolver;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;
import static java.lang.Character.toUpperCase;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Default implementation to resolve the dataprovider method for a given {@link DataProviderResolverContext}.
 * <p>
 * The <b>location</b>s of the dataprovider are retrieved from given {@link DataProviderResolverContext#getLocations()}.
 * <p>
 * The <b>name</b> of the dataprovider method can be explicitly set via
 * {@link DataProviderResolverContext#getDataProviderName()} or derived by convention if name is equal to
 * {@link DataProviderResolverContext#METHOD_NAME_TO_USE_CONVENTION}. Here are the applied strategies for dataprovider
 * method name resolution:
 * <ul>
 * <li>Explicitly configured name using {@link DataProviderResolverContext#getDataProviderName()}. (no fallback if
 * dataprovider could not be found)</li>
 * <li>Dataprovider method which name equals the test method name</li>
 * <li>Dataprovider method whereby prefix is replaced by one out of the following:
 * <table border="1">
 * <caption>Prefix replacement overview</caption>
 * <tr>
 * <th>prefix</th>
 * <th>replacement</th>
 * </tr>
 * <tr>
 * <td>test</td>
 * <td>dataProvider</td>
 * </tr>
 * <tr>
 * <td>test</td>
 * <td>data</td>
 * </tr>
 * </table>
 * </li>
 * <li>Dataprovider method whereby additional prefix "dataProvider" or "data" is given. Also the first letter of the
 * original test method name is uppercased, e.g. {@code shouldReturnTwoForOnePlusOne} corresponds to
 * {@code dataProviderShouldReturnTwoForOnePlusOne}.</li>
 * </ul>
 * <p>
 * Note: Dataproviders are also found if they are not public.
 */
public class DefaultDataProviderMethodResolver implements DataProviderMethodResolver {

    /**
     * {@inheritDoc}
     *
     * @see DefaultDataProviderMethodResolver
     */
    @Override
    public List<Method> resolve(DataProviderResolverContext context) {
        checkNotNull(context, "'context' must not be null");

        List<Method> dataProviderMethods = findAnnotatedMethods(context.getLocations(),
                context.getDataProviderAnnotationClass());

        List<Method> result = new ArrayList<Method>();
        for (Method dataProviderMethod : dataProviderMethods) {
            if (context.useDataProviderNameConvention()) {
                if (isMatchingNameConvention(context.getTestMethod().getName(), dataProviderMethod.getName())) {
                    result.add(dataProviderMethod);
                }
            } else if (dataProviderMethod.getName().equals(context.getDataProviderName())) {
                result.add(dataProviderMethod);
            }
        }
        return result;
    }

    /**
     * Searches methods annotated in the given locations one after another. Thereby all methods which are annotated with
     * the given annotation are searched in the respective class hierarchy completely from the given location (= child
     * class) down to {@link Object} (= parent class).
     * <p>
     * Note:
     * <ul>
     * <li>Resulting methods are always sorted the same (see {@link #sorted(Method[])}).</li>
     * <li>Shadowing check is only applied location by location, not location overarching.</li>
     * </ul>
     *
     * @param locations denote where to search for methods
     * @param annotationClass which found methods should contain
     * @return the found methods or an empty list
     */
    protected List<Method> findAnnotatedMethods(List<Class<?>> locations, Class<? extends Annotation> annotationClass) {
        List<Method> result = new ArrayList<Method>();
        for (Class<?> location : locations) {
            List<Method> intermediateResult = new ArrayList<Method>();
            Class<?> currentClass = location;
            while (currentClass != null) {
                for (Method method : sorted(currentClass.getDeclaredMethods())) {
                    Annotation foundAnnotation = method.getAnnotation(annotationClass);
                    if (foundAnnotation != null && !isMethodShadowedBy(method, intermediateResult)) {
                        intermediateResult.add(method);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
            result.addAll(intermediateResult);
        }
        return result;
    }

    protected boolean isMatchingNameConvention(String testMethodName, String dataProviderMethodName) {
        if (dataProviderMethodName.equals(testMethodName)) {
            return true;
        } else if (dataProviderMethodName.equals(testMethodName.replaceAll("^test", "dataProvider"))) {
            return true;
        } else if (dataProviderMethodName.equals(testMethodName.replaceAll("^test", "data"))) {
            return true;
        } else if (dataProviderMethodName
                .equals("dataProvider" + toUpperCase(testMethodName.charAt(0)) + testMethodName.substring(1))) {
            return true;
        } else if (dataProviderMethodName
                .equals("data" + toUpperCase(testMethodName.charAt(0)) + testMethodName.substring(1))) {
            return true;
        }
        return false;
    }

    /**
     * Method sorter to get a predicatable order of dataproviders because of
     * <a href="bugs.java.com/view_bug.do?bug_id=7023180">JDK-7023180 : Change in specified-to-be-unspecified ordering
     * of getDeclaredMethods causes application problems</a>.
     *
     * @param methods to be sorted
     * @return a sorted copy of methods
     */
    private Method[] sorted(Method[] methods) {
        // Comparator for {@link Method}s based upon JUnit 4's {@code org.junit.internal.MethodSorter} implementation.
        Comparator<Method> defaultSorter = new Comparator<Method>() {
            @Override
            public int compare(Method method1, Method method2) {
                String name1 = method1.getName();
                String name2 = method2.getName();
                int comparison = Integer.compare(name1.hashCode(), name2.hashCode());
                if (comparison == 0) {
                    comparison = name1.compareTo(name2);
                    if (comparison == 0) {
                        comparison = method1.toString().compareTo(method2.toString());
                    }
                }
                return comparison;
            }
        };

        Method[] result = Arrays.copyOf(methods, methods.length);
        Arrays.sort(result, defaultSorter);
        return result;
    }

    private boolean isMethodShadowedBy(Method method, List<Method> methods) {
        for (Method other : methods) {
            if (!other.getName().equals(method.getName())) {
                continue;
            }
            if (!Arrays.equals(other.getParameterTypes(), method.getParameterTypes())) {
                continue;
            }
            return true;
        }
        return false;
    }
}
