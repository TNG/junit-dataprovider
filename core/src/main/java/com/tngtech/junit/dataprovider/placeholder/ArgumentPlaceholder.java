package com.tngtech.junit.dataprovider.placeholder;

import java.util.List;

/**
 * This placeholder format the arguments of a dataprovider test as comma-separated {@link String} according to the given
 * index or range subscript. For a list of special argument treatments, see {@link AbstractArgumentPlaceholder}.
 *
 * @see AbstractArgumentPlaceholder
 */
public class ArgumentPlaceholder extends AbstractArgumentPlaceholder {

    public ArgumentPlaceholder() {
        super("%[ap]\\[(-?[0-9]+|-?[0-9]+\\.\\.-?[0-9]+)\\]");
    }

    @Override
    protected String getReplacementFor(String placeholder, ReplacementData data) {
        FromAndTo fromAndTo = calcFromAndToForSubscriptAndArguments(placeholder, 3, data.getArguments().size());
        return formatAll(data.getArguments().subList(fromAndTo.from, fromAndTo.to));
    }

    /**
     * Formats the given arguments by retrieving it's {@link String} representation and separate it by comma (=
     * {@code ,}).
     *
     * @param arguments to be formatted
     * @return the {@link String} representation of the given {@link List}{@code <Object>}
     */
    protected String formatAll(List<Object> arguments) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arguments.size(); i++) {
            stringBuilder.append(format(arguments.get(i)));
            if (i < arguments.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }
}
