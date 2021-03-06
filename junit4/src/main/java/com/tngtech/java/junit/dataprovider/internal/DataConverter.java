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
package com.tngtech.java.junit.dataprovider.internal;

import static com.tngtech.java.junit.dataprovider.common.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.internal.convert.ObjectArrayConverter;
import com.tngtech.java.junit.dataprovider.internal.convert.SingleArgConverter;
import com.tngtech.java.junit.dataprovider.internal.convert.StringConverter;
import com.tngtech.junit.dataprovider.convert.ConverterContext;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal class to convert some data to its corresponding parameters.
 */
@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS", justification = "for backwards compatibility and easier migration to core")
public class DataConverter extends com.tngtech.junit.dataprovider.convert.DataConverter {

    private ObjectArrayConverter objectArrayConverter;
    private SingleArgConverter singleArgConverter;
    private StringConverter stringConverter;

    public DataConverter() {
        this.objectArrayConverter = new ObjectArrayConverter();
        this.singleArgConverter = new SingleArgConverter();
        this.stringConverter = new StringConverter();
    }

    /**
     * Returns {@code true} iif this {@link DataConverter} can convert the given {@code type}. Currently supported {@code type}s:
     * <ul>
     * <li>Object[][]</li>
     * <li>Iterable&lt;Iterable&lt;?&gt;&gt;</li>
     * <li>Iterable&lt;?&gt;</li>
     * <li>Object[]</li>
     * <li>String[]</li>
     * </ul>
     *
     * Please note, that {@link Iterable} can be replaced by any valid subtype (checked via {@link Class#isAssignableFrom(Class)}). As well
     * as an arbitrary inner type is also accepted.
     *
     * @param type to be checked for convertibility (use either {@link Method#getGenericReturnType()}, {@link Method#getReturnType()}, or
     *            simple {@link Class} if possible)
     * @return {@code true} iif given {@code type} can be converted.
     */
    public boolean canConvert(Type type) {
        if (type instanceof Class) {
            return Object[][].class.equals(type) || Object[].class.equals(type) || String[].class.equals(type);
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return Iterable.class.isAssignableFrom((Class<?>) parameterizedType.getRawType());
        }
        return false;
    }

    /**
     * Converts the given {@link Object} to a {@link List} of {@link Object}{@code []} with {@link Class}es correspond
     * to given {@code parameterTypes}.
     * <p>
     * For compatible types, see {@link #canConvert(Type)}.
     *
     * @param data to be converted
     * @param isVarargs determines whether test method has a varargs parameter
     * @param parameterTypes required types for {@code data}
     * @param dataProvider containing settings which should be used to convert given {@code data}
     * @return converted data as {@link List}{@code <}{@link Object}{@code []>} with the required {@code parameterTypes}
     * @throws NullPointerException iif given {@code parameterTypes} or {@code settings} are {@code null}
     * @throws IllegalArgumentException iif given {@code parameterTypes} is empty
     * @throws ClassCastException iif {@code data} is not a compatible type
     */
    public List<Object[]> convert(Object data, boolean isVarargs, Class<?>[] parameterTypes, DataProvider dataProvider) {
        checkNotNull(dataProvider, "dataProvider must not be null");

        ConverterContext context = new ConverterContext(objectArrayConverter, singleArgConverter, stringConverter,
                dataProvider.splitBy(), dataProvider.convertNulls(), dataProvider.trimValues(),
                dataProvider.ignoreEnumCase());
        return super.convert(data, isVarargs, parameterTypes, context);
    }

    public void setObjectArrayConverter(ObjectArrayConverter objectArrayConverter) {
        this.objectArrayConverter = objectArrayConverter;
    }

    public void setSingleArgConverter(SingleArgConverter singleArgConverter) {
        this.singleArgConverter = singleArgConverter;
    }

    public void setStringConverter(StringConverter stringConverter) {
        this.stringConverter = stringConverter;
    }
}
