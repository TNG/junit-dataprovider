package com.tngtech.java.junit.dataprovider.internal.convert;

import java.lang.annotation.Annotation;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.format.DataProviderTestNameFormatter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @deprecated Use {@link com.tngtech.junit.dataprovider.convert.StringConverter} of {@code core/} instead. JUnit4
 *             internals can handle both. This class will be removed in version 3.0.
 */
@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS", justification = "for backwards compatibility and easier migration to core")
@Deprecated
public class StringConverter extends com.tngtech.junit.dataprovider.convert.StringConverter {

    protected static final Object OBJECT_NO_CONVERSION = com.tngtech.junit.dataprovider.convert.StringConverter.OBJECT_NO_CONVERSION;

    /**
     * Converts the given {@code data} to its corresponding arguments using the given {@code parameterTypes} and other
     * provided information.
     *
     * @param data regex-separated {@link String} of parameters for test method
     * @param isVarargs determines whether test method has a varargs parameter
     * @param parameterTypes target types of parameters to which corresponding values in regex-separated {@code data}
     *            should be converted
     * @param dataProvider containing settings which should be used to convert given {@code data}
     * @param rowIdx index of current {@code data} (row) for better error messages
     * @return split, trimmed and converted {@code Object[]} of supplied regex-separated {@code data}
     * @throws IllegalArgumentException iif count of split data and parameter types does not match or argument cannot be
     *             converted to required type
     */
    public Object[] convert(String data, boolean isVarargs, Class<?>[] parameterTypes, DataProvider dataProvider,
            int rowIdx) {
        ConverterContext context = new ConverterContext(dataProvider.splitBy(), dataProvider.convertNulls(),
                dataProvider.trimValues(), dataProvider.ignoreEnumCase());
        return super.convert(data, isVarargs, parameterTypes, context, rowIdx);
    }

    @Override
    protected String[] splitBy(String data, String regex) {
        return super.splitBy(data, regex);
    }

    @Override
    protected void checkArgumentsAndParameterCount(int argCount, int paramCount, boolean isVarargs, int rowIdx) {
        if ((isVarargs && paramCount - 1 > argCount) || (!isVarargs && paramCount != argCount)) {
            throw new IllegalArgumentException(
                    String.format("Test method expected %s%d parameters but got %d arguments in row %d",
                            (isVarargs) ? "at least " : "", paramCount - (isVarargs ? 1 : 0), argCount, rowIdx));
        }
        super.checkArgumentsAndParameterCount(argCount, paramCount, isVarargs, rowIdx);
    }

    @Override
    protected Object customConvertValue(String str, Class<?> targetType, ConverterContext context) {
        DataProvider dataProvider = createProxyDataProvider(context);
        return customConvertValue(str, targetType, dataProvider);
    }

    /**
     * This method purely exists as potential extension point by overriding it.
     *
     * @param str value to be converted
     * @param targetType target type into which value should be converted
     * @param dataProvider containing settings which should be used to convert given {@code data}
     * @return to target type converted {@link String} or {@link #OBJECT_NO_CONVERSION} if no conversion was applied.
     *         Later will imply that normal conversions try to apply.
     */
    protected Object customConvertValue(String str, Class<?> targetType, DataProvider dataProvider) {
        return OBJECT_NO_CONVERSION;
    }

    @Override
    protected Object convertPrimaryOrWrapper(String str, Class<?> targetType) {
        return super.convertPrimaryOrWrapper(str, targetType);
    }

    @Override
    protected Object convertToLong(String str) {
        return super.convertToLong(str);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected Object convertToEnumValue(String str, Class<Enum> enumType, boolean ignoreEnumCase) {
        return super.convertToEnumValue(str, enumType, ignoreEnumCase);
    }

    @Override
    protected Object tryConvertUsingSingleStringParamConstructor(String str, Class<?> targetType) {
        return super.tryConvertUsingSingleStringParamConstructor(str, targetType);
    }

    private DataProvider createProxyDataProvider(final ConverterContext context) {
        return new DataProvider() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return DataProvider.class;
            }

            @Override
            public String[] value() {
                throw new UnsupportedOperationException(
                        "Sorry, this operation is not available anymore. Please create an issue if you still need it.");
            }

            @Override
            public boolean trimValues() {
                return context.isTrimValues();
            }

            @Override
            public String splitBy() {
                return context.getSplitBy();
            }

            @Override
            public boolean ignoreEnumCase() {
                return context.isIgnoreEnumCase();
            }

            @Override
            public boolean cache() {
                throw new UnsupportedOperationException(
                        "Sorry, this operation is not available anymore. Please create an issue if you still need it.");
            }

            @Override
            public String format() {
                throw new UnsupportedOperationException(
                        "Sorry, this operation is not available anymore. Please create an issue if you still need it.");
            }

            @Override
            public Class<? extends DataProviderTestNameFormatter> formatter() {
                throw new UnsupportedOperationException(
                        "Sorry, this operation is not available anymore. Please create an issue if you still need it.");
            }

            @Override
            public boolean convertNulls() {
                return context.isConvertNulls();
            }
        };
    }
}
