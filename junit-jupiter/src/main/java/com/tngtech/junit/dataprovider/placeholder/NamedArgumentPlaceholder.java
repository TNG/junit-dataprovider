package com.tngtech.junit.dataprovider.placeholder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * This placeholder format the arguments including their parameter names of a dataprovider test as comma-separated
 * {@link String} according to the given index or range subscript. For a list of special argument treatments, see
 * {@link AbstractArgumentPlaceholder}.
 *
 * @see AbstractArgumentPlaceholder
 */
public class NamedArgumentPlaceholder extends AbstractArgumentPlaceholder {

    private static final Logger logger = Logger.getLogger(NamedArgumentPlaceholder.class.getName());

    public NamedArgumentPlaceholder() {
        super("%na\\[(-?[0-9]+|-?[0-9]+\\.\\.-?[0-9]+)\\]");
    }

    @Override
    protected String getReplacementFor(String placeholder, ReplacementData data) {
        FromAndTo fromAndTo = calcFromAndToForSubscriptAndArguments(placeholder, 4, data.getArguments().size());
        return formatAll(getSubArrayOfMethodParameters(data.getTestMethod(), fromAndTo),
                data.getArguments().subList(fromAndTo.from, fromAndTo.to));
    }

    /**
     * Formats the given parameters and arguments to a comma-separated list of {@code $parameterName=$argumentName}.
     * Arguments {@link String} representation are therefore treated specially.
     *
     * @param parameters used to for formatting
     * @param arguments to be formatted
     * @return the formatted {@link String} of the given {@link Parameter}{@code []} and {@link List}{@code <Object>}
     */
    protected String formatAll(Parameter[] parameters, List<Object> arguments) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int idx = 0; idx < arguments.size(); idx++) {
            String parameterName = (parameters.length > idx) ? parameters[idx].getName() : "?";
            Object argument = arguments.get(idx);

            stringBuilder.append(parameterName).append("=").append(format(argument));
            if (idx < arguments.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    private Parameter[] getSubArrayOfMethodParameters(Method testMethod, FromAndTo fromAndTo) {
        Parameter[] parameters = testMethod.getParameters();
        if (parameters.length > 0 && !parameters[0].isNamePresent()) {
            logger.warning(String.format("Parameter names on method '%s' are not available"
                    + ". To store formal parameter names, compile the source file with the '-parameters' option"
                    + ". See also https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html",
                    testMethod));
        }
        return Arrays.copyOfRange(parameters, fromAndTo.from, fromAndTo.to);
    }
}
