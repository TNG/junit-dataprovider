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
package com.tngtech.java.junit.dataprovider.internal.convert;

import static com.tngtech.junit.dataprovider.Preconditions.checkArgument;
import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS", justification = "for backwards compatibility and easier migration to core")
public class SingleArgConverter extends com.tngtech.junit.dataprovider.convert.SingleArgConverter {

    @Override
    public Object[] convert(Object data, boolean isVarargs, Class<?>[] parameterTypes) {
        checkArgument(parameterTypes.length == 1, "Object[] dataprovider just supports single argument test method but found %d parameters",
                parameterTypes.length);
        return super.convert(data, isVarargs, parameterTypes);
    }

    @Override
    protected void checkIfArgumentsMatchParameterTypes(Object[] arguments, Class<?>[] parameterTypes) {
        checkNotNull(arguments, "'arguments' must not be null");
        checkNotNull(parameterTypes, "'testMethod' must not be null");
        checkArgument(parameterTypes.length == arguments.length,
                "Expected %d arguments for test method but got %d parameters.", arguments.length,
                parameterTypes.length);
        super.checkIfArgumentsMatchParameterTypes(arguments, parameterTypes);
    }
}
