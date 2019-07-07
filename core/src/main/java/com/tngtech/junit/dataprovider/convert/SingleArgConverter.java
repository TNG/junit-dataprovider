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

import static com.tngtech.junit.dataprovider.Preconditions.checkArgument;

public class SingleArgConverter extends AbstractObjectConverter<Object> {

    /**
     * {@inheritDoc}
     *
     * @param data argument for test method
     * @param isVarargs determines whether test method has a varargs parameter
     * @param parameterTypes target types of parameters
     * @return {@code Object[]} which is converted and checked
     * @throws IllegalArgumentException if and only if the parameter size does not fit, test method has varargs, or
     *             there is a type mismatch
     */
    @Override
    public Object[] convert(Object data, boolean isVarargs, Class<?>[] parameterTypes) {
        checkArgument(parameterTypes.length >= 1,
                "Object[] dataprovider must at least have a single argument for the dataprovider but found no parameters");
        checkArgument(!isVarargs, "Object[] dataprovider does not support varargs");

        Object[] result = new Object[] { data };
        checkIfArgumentsMatchParameterTypes(result, parameterTypes);
        return result;
    }
}
