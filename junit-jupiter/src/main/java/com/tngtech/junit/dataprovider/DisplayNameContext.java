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
package com.tngtech.junit.dataprovider;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.tngtech.junit.dataprovider.format.DataProviderTestNameFormatter;
import com.tngtech.junit.dataprovider.placeholder.BasePlaceholder;

public class DisplayNameContext {

    private final Class<? extends DataProviderTestNameFormatter> formatter;
    private final String format;
    private final List<? extends BasePlaceholder> placeholders;

    public DisplayNameContext(String format, List<? extends BasePlaceholder> placeholders) {
        this(null, format, placeholders);
    }

    public DisplayNameContext(Class<? extends DataProviderTestNameFormatter> formatter, String format,
            List<? extends BasePlaceholder> placeholders) {
        this.formatter = formatter;
        this.format = checkNotNull(format, "'format' must not be null");
        this.placeholders = new ArrayList<>(checkNotNull(placeholders, "'placeholders' must not be null"));
    }

    public Class<? extends DataProviderTestNameFormatter> getFormatter() {
        return formatter;
    }

    public String getFormat() {
        return format;
    }

    public List<? extends BasePlaceholder> getPlaceholders() {
        return unmodifiableList(placeholders);
    }
}
