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
package com.tngtech.java.junit.dataprovider.common;

public class Preconditions {

    public static <T> T checkNotNull(T object, String errorMessage) {
        return com.tngtech.junit.dataprovider.Preconditions.checkNotNull(object, errorMessage);
    }

    public static void checkArgument(boolean expression, String errorMessage) {
        com.tngtech.junit.dataprovider.Preconditions.checkArgument(expression, errorMessage);
    }

    public static void checkArgument(boolean expression, String errorMessageFormat, Object... errorMessageArgs) {
        com.tngtech.junit.dataprovider.Preconditions.checkArgument(expression, errorMessageFormat,
                errorMessageArgs);
    }
}
