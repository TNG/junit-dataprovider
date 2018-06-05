package com.tngtech.java.junit.dataprovider;

import static com.tngtech.junit.dataprovider.Preconditions.checkArgument;
import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.runners.model.FrameworkMethod;

import com.tngtech.java.junit.dataprovider.format.DataProviderPlaceholderFormatter;
import com.tngtech.junit.dataprovider.format.DataProviderTestNameFormatter;

/**
 * A special framework method that allows the usage of parameters for the test method.
 */
public class DataProviderFrameworkMethod extends FrameworkMethod {

    /**
     * Index of exploded test method such that each get a unique name.
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    final int idx;

    /**
     * Parameters to invoke the test method.
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    final Object[] parameters;

    /**
     * Format of test method name.
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    final String nameFormat;

    /**
     * Formatter for test method name. May be {@code null}.
     * <p>
     * This field is package private (= visible) for testing.
     * </p>
     */
    final Class<? extends DataProviderTestNameFormatter> nameFormatter;

    /**
     * Create a {@link FrameworkMethod} extended with special attributes for using this test with a dataprovider.
     *
     * @param method test method for which the {@link FrameworkMethod} is created
     * @param idx the index (row) of the used dataprovider
     * @param parameters used for invoking this test method
     * @param nameFormat defines the format of the test method name according to {@code @}{@link DataProvider#format()}
     */
    public DataProviderFrameworkMethod(Method method, int idx, Object[] parameters, String nameFormat) {
        this(method, idx, parameters, nameFormat, null);
    }

    /**
     * Create a {@link FrameworkMethod} extended with special attributes for using this test with a dataprovider.
     *
     * @param method test method for which the {@link FrameworkMethod} is created
     * @param idx the index (row) of the used dataprovider
     * @param parameters used for invoking this test method
     * @param nameFormat defines the format of the test method name according to {@code @}{@link DataProvider#format()}
     * @param nameFormatter defines the test method name formatter
     */
    public DataProviderFrameworkMethod(Method method, int idx, Object[] parameters,
            String nameFormat, Class<? extends DataProviderTestNameFormatter> nameFormatter) {
        super(method);

        checkNotNull(parameters, "parameter must not be null");
        checkNotNull(nameFormat, "nameFormat must not be null");
        checkArgument(parameters.length != 0, "parameter must not be empty");

        this.idx = idx;
        this.parameters = Arrays.copyOf(parameters, parameters.length);
        this.nameFormat = nameFormat;
        this.nameFormatter = nameFormatter;
    }

    @Override
    public String getName() {
        if (nameFormatter == null || DataProviderPlaceholderFormatter.class.equals(nameFormatter)) {
            return new DataProviderPlaceholderFormatter(nameFormat, Placeholders.all()).format(getMethod(), idx,
                    Arrays.asList(parameters));
        }

        try {
            DataProviderTestNameFormatter instance = null;
            for (Constructor<?> constructor : nameFormatter.getConstructors()) {
                if (constructor.getParameterCount() == 1 && String.class.equals(constructor.getParameterTypes()[0])) {
                    instance = (DataProviderTestNameFormatter) constructor.newInstance(nameFormat);
                    break;
                }
            }
            if (instance == null) {
                instance = nameFormatter.newInstance();
            }
            return instance.format(getMethod(), idx, Arrays.asList(parameters));
        } catch (InstantiationException e) {
            throw new IllegalStateException(String
                    .format("Could not instantiate name formatter using default constructor of '%s'.", nameFormatter),
                    e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(
                    String.format("Default constructor not accessable of name formatter '%s'.", nameFormatter), e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(String.format("Default constructor of name formatter '%s' has thrown: %s",
                    nameFormatter, e.getMessage()), e);
        } catch (Exception e) {
            throw new IllegalStateException(String.format(
                    "Unexpected exception while finding and invoking default constructor of name formatter '%s': %s",
                    nameFormatter, e.getMessage()), e);
        }
    }

    @Override
    public Object invokeExplosively(Object target, Object... params) throws Throwable {
        return super.invokeExplosively(target, parameters);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + idx;
        result = prime * result + ((nameFormat == null) ? 0 : nameFormat.hashCode());
        result = prime * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DataProviderFrameworkMethod other = (DataProviderFrameworkMethod) obj;
        if (idx != other.idx) {
            return false;
        }
        if (nameFormat == null) {
            if (other.nameFormat != null) {
                return false;
            }
        } else if (!nameFormat.equals(other.nameFormat)) {
            return false;
        }
        if (!Arrays.equals(parameters, other.parameters)) {
            return false;
        }
        return true;
    }
}
