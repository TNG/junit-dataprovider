/*
 * Copyright 2019 TNG Technology Consulting GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
