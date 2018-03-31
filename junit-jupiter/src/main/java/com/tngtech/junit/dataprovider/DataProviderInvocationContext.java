package com.tngtech.junit.dataprovider;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;
import static java.util.Collections.singletonList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.platform.commons.util.ReflectionUtils;

import com.tngtech.junit.dataprovider.format.DataProviderPlaceholderFormatter;
import com.tngtech.junit.dataprovider.format.DataProviderTestNameFormatter;

class DataProviderInvocationContext implements TestTemplateInvocationContext {

    private final Method testMethod;
    private final List<Object> arguments;
    private final DisplayNameContext displayNameContext;

    DataProviderInvocationContext(Method testMethod, List<Object> arguments, DisplayNameContext displayNameContext) {
        this.testMethod = checkNotNull(testMethod, "'testMethod' must not be null");
        this.arguments = new ArrayList<>(checkNotNull(arguments, "'arguments' must not be null"));
        this.displayNameContext = checkNotNull(displayNameContext, "'displayNameContext' must not be null");
    }

    @Override
    public String getDisplayName(int invocationIndex) {

        if (displayNameContext.getFormatter() == null
                || DataProviderPlaceholderFormatter.class.equals(displayNameContext.getFormatter())) {
            return new DataProviderPlaceholderFormatter(displayNameContext.getFormat(),
                    displayNameContext.getPlaceholders()).format(testMethod, invocationIndex, arguments);
        }
        return ReflectionUtils.newInstance(displayNameContext.getFormatter()).format(testMethod, invocationIndex,
                arguments);
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
        return singletonList(new DataProviderParameterResolver(arguments));
    }
}
