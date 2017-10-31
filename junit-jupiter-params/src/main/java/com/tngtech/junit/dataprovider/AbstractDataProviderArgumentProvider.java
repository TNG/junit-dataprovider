package com.tngtech.junit.dataprovider;

import static com.tngtech.junit.dataprovider.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import com.tngtech.junit.dataprovider.convert.ConverterContext;
import com.tngtech.junit.dataprovider.convert.DataConverter;

/**
 * Abstract, internal argument provider to create {@link Stream} of {@link Arguments}s for dataprovider tests.
 *
 * @param <SOURCE_ANNOTATION> annotation type used on test method for which this argument provider is concerned
 */
abstract class AbstractDataProviderArgumentProvider<SOURCE_ANNOTATION extends Annotation>
        implements ArgumentsProvider, AnnotationConsumer<SOURCE_ANNOTATION> {

    /**
     * The {@link DataConverter} to be used to convert from supported return types of any dataprovider to {@link List}
     * {@code <}{@link Object}{@code []>} such that data can be further handled. Defaults to {@link DataConverter}.
     */
    private final DataConverter dataConverter;

    /**
     * The annotation used on the test method where dataprovider tests should be generated for.
     */
    protected SOURCE_ANNOTATION sourceAnnotation;

    protected AbstractDataProviderArgumentProvider(DataConverter dataConverter) {
        this.dataConverter = dataConverter;
    }

    protected AbstractDataProviderArgumentProvider() {
        this(new DataConverter());
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note:</b> needs to be overridden because otherwise
     * {@link org.junit.jupiter.params.support.AnnotationConsumerInitializer} will not recognize it as {@code accept}
     * method which takes a single annotation argument. The implementation should be
     *
     * <pre>
     * <code>this.sourceAnnotation = sourceAnnotation;</code>
     * </pre>
     */
    @Override
    public abstract void accept(SOURCE_ANNOTATION sourceAnnotation);

    /**
     * Converts the given data for the given test method and converter context.
     *
     * @param testMethod the original test method for which the data is converted; never {@code null}
     * @param data the data to be converted; never {@code null}
     * @param context the converter context to be used to do the data conversion; never {@code null}
     * @return a {@link Stream} of properly converted arguments; never {@code null}
     * @throws NullPointerException if and only if one of the given arguments is {@code null}
     */
    protected Stream<? extends Arguments> convertData(Method testMethod, Object data, ConverterContext context) {
        checkNotNull(testMethod, "'testMethod' must not be null");
        checkNotNull(data, "'data' must not be null");
        checkNotNull(context, "'context' must not be null");

        return dataConverter.convert(data, testMethod.isVarArgs(), testMethod.getParameterTypes(), context).stream()
                .map(objects -> {
                    Class<?>[] parameterTypes = testMethod.getParameterTypes();
                    for (int idx = 0; idx < objects.length; idx++) {
                        // TODO workaround for https://github.com/junit-team/junit5/issues/1092
                        Class<?> parameterType = parameterTypes[idx];
                        if (parameterType.isPrimitive()) {
                            objects[idx] = convertToBoxedTypeAsWorkaroundForNotWorkingWideningAndUnboxingConversion(
                                    objects[idx], parameterType);
                        }
                    }
                    return objects;
                }).map(Arguments::of);
    }

    private Object convertToBoxedTypeAsWorkaroundForNotWorkingWideningAndUnboxingConversion(Object result,
            Class<?> parameterType) {
        if (short.class.equals(parameterType)) {
            return ((Number) result).shortValue();
        } else if (byte.class.equals(parameterType)) {
            return ((Number) result).byteValue();
        } else if (int.class.equals(parameterType)) {
            return ((Number) result).intValue();
        } else if (long.class.equals(parameterType)) {
            return ((Number) result).longValue();
        } else if (float.class.equals(parameterType)) {
            return ((Number) result).floatValue();
        } else if (double.class.equals(parameterType)) {
            return ((Number) result).doubleValue();
        }
        return result;
    }
}
