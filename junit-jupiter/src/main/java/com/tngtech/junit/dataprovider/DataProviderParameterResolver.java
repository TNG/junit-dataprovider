package com.tngtech.junit.dataprovider;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

class DataProviderParameterResolver implements ParameterResolver {

    private final List<Object> arguments;

    DataProviderParameterResolver(List<Object> arguments) {
        this.arguments = new ArrayList<>(checkNotNull(arguments, "'arguments' must not be null"));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Executable declaringExecutable = parameterContext.getParameter().getDeclaringExecutable();
        Method testMethod = extensionContext.getTestMethod().orElse(null);
        return declaringExecutable.equals(testMethod) && parameterContext.getIndex() < arguments.size();
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return arguments.get(parameterContext.getIndex());
    }
}
