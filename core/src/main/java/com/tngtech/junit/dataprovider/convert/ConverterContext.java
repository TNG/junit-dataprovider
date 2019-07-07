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
package com.tngtech.junit.dataprovider.convert;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;

/**
 * Context for the dataprovider conversion to arguments. It holds configurations to convert dataprovider {@code data} to
 * proper test data, the special converters to be used, and so on.
 */
public class ConverterContext {

    /**
     * Default delimiter is a comma to split up arguments for dataprovider tests.
     *
     * @see #getSplitBy()
     */
    public static final String COMMA = ",";

    /**
     * {@code null}-{@link String} value to be converted to {@code null} if {@link #isConvertNulls()} is {@code true}.
     */
    public static final String NULL = "null";

    /**
     * The delimiting regular expression by which the regex-separated {@link String}s are split. Defaults to
     * {@value #COMMA}.
     *
     * @return the regex to split {@link String} {@code data}
     * @see String#split(String)
     */
    private final String splitBy;

    /**
     * Determines if every {@value #NULL}-{@link String} in dataprovider's {@link String} {@code data} should be
     * converted to {@code null} (= {@code true}) or used as {@link String} (= {@code false}). Default is {@code true}.
     *
     * @return {@code true} if and only if "null"-{@link String}s should be converted to {@code null}.
     */
    private final boolean convertNulls;

    /**
     * {@code true} if leading and trailing whitespace should be omitted in split {@link String}s, {@code false}
     * otherwise. Default is {@code true}.
     *
     * @return {@code true} if and only if regex-separated {@link String} data should be trimmed
     * @see String#trim()
     */
    private final boolean trimValues;

    /**
     * {@code true} if and only if the case for {@link Enum} conversion should be ignored such that searching for the
     * corresponding {@link Enum} values is case-insensitive. Default is {@code false}.
     */
    private final boolean ignoreEnumCase;

    /**
     * {@link ObjectArrayConverter} to use for data conversion. Defaults to instance of {@link ObjectArrayConverter}.
     */
    private final ObjectArrayConverter objectArrayConverter;

    /**
     * {@link SingleArgConverter} to use for data conversion. Defaults to instance of {@link SingleArgConverter}.
     */
    private final SingleArgConverter singleArgConverter;

    /**
     * {@link StringConverter} to use for data conversion. Defaults to instance of {@link StringConverter}.
     */
    private final StringConverter stringConverter;

    public ConverterContext(ObjectArrayConverter objectArrayConverter, SingleArgConverter singleArgConverter,
            StringConverter stringConverter, String splitBy, boolean convertNulls, boolean trimValues,
            boolean ignoreEnumCase) {
        this.splitBy = checkNotNull(splitBy, "'splitBy' must not be null");
        this.convertNulls = convertNulls;
        this.trimValues = trimValues;
        this.ignoreEnumCase = ignoreEnumCase;
        this.objectArrayConverter = checkNotNull(objectArrayConverter, "'objectArrayConverter' must not be null");
        this.singleArgConverter = checkNotNull(singleArgConverter, "'singleArgConverter' must not be null");
        this.stringConverter = checkNotNull(stringConverter, "'stringConverter' must not be null");
    }

    public ConverterContext(String splitBy, boolean convertNulls, boolean trimValues, boolean ignoreEnumCase) {
        this(new ObjectArrayConverter(), new SingleArgConverter(), new StringConverter(), splitBy, convertNulls,
                trimValues, ignoreEnumCase);
    }

    /**
     * @return the delimiting regular expression by which the regex-separated {@link String}s are split.
     */
    public String getSplitBy() {
        return splitBy;
    }

    /**
     * @return if every {@value #NULL}-{@link String} in dataprovider's {@link String} {@code data} should be converted
     *         to {@code null} (= {@code true}) or used as {@link String} (= {@code false}).
     */
    public boolean isConvertNulls() {
        return convertNulls;
    }

    /**
     * @return {@code true} if leading and trailing whitespace should be omitted in split {@link String}s, {@code false}
     *         otherwise.
     */
    public boolean isTrimValues() {
        return trimValues;
    }

    /**
     * @return {@code true} if and only if the case for {@link Enum} conversion should be ignored such that searching
     *         for the corresponding {@link Enum} values is case-insensitive.
     */
    public boolean isIgnoreEnumCase() {
        return ignoreEnumCase;
    }

    /**
     * @return the {@link ObjectArrayConverter} to use for data conversion. Defaults to instance of
     *         {@link ObjectArrayConverter}.
     */
    public ObjectArrayConverter getObjectArrayConverter() {
        return objectArrayConverter;
    }

    /**
     * @return the {@link SingleArgConverter} to use for data conversion. Defaults to instance of
     *         {@link SingleArgConverter}.
     */
    public SingleArgConverter getSingleArgConverter() {
        return singleArgConverter;
    }

    /**
     * @return the {@link StringConverter} to use for data conversion. Defaults to instance of {@link StringConverter}.
     */
    public StringConverter getStringConverter() {
        return stringConverter;
    }
}
