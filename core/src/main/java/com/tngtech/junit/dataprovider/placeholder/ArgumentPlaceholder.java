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
        String subscript = placeholder.substring(3, placeholder.length() - 1);

        int from = Integer.MAX_VALUE;
        int to = Integer.MIN_VALUE;
        if (subscript.contains("..")) {
            String[] split = subscript.split("\\.\\.");

            from = Integer.parseInt(split[0]);
            to = Integer.parseInt(split[1]);
        } else {
            from = Integer.parseInt(subscript);
            to = from;
        }

        List<Object> arguments = data.getArguments();
        from = (from >= 0) ? from : arguments.size() + from;
        to = (to >= 0) ? to + 1 : arguments.size() + to + 1;
        return formatAll(arguments.subList(from, to));
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
