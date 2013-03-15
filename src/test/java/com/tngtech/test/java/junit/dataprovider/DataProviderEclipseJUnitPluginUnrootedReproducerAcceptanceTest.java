package com.tngtech.test.java.junit.dataprovider;

import static java.util.Arrays.asList;

import java.util.List;

import javax.xml.bind.ValidationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class DataProviderEclipseJUnitPluginUnrootedReproducerAcceptanceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @DataProvider
    public static Object[][] dataProviderValidateDto() {
        // @formatter:off
            return new Object[][] {
                    { asList(new DtoNotWorking(1, -12)), ValidationException.class },
                    { asList(new DtoNotWorking(1, -1)),  ValidationException.class },

                    { asList(new DtoNotWorking(1, 0)), null },
                    { asList(new DtoNotWorking(1, 1)), null },
            };
            // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderValidateDto")
    public void testValidateDto(List<DtoNotWorking> objects, Class<? extends Exception> exception) throws Exception {

        if (exception != null) {
            expectedException.expect(exception);
        }

        // Given:

        // When:
        validateDtos(objects);

        // Then:
    }

    @DataProvider
    public static Object[][] dataProviderValidateDtoWithToString() {
        // @formatter:off
        return new Object[][] {
                { asList(new DtoWorking(1, -12)), ValidationException.class },
                { asList(new DtoWorking(1, -1)),  ValidationException.class },

                { asList(new DtoWorking(1, 0)), null },
                { asList(new DtoWorking(1, 1)), null },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderValidateDtoWithToString")
    public void testValidateDtoWithToString(List<DtoNotWorking> objects, Class<? extends Exception> exception)
            throws Exception {

        if (exception != null) {
            expectedException.expect(exception);
        }

        // Given:

        // When:
        validateDtos(objects);

        // Then:
    }

    private void validateDtos(List<DtoNotWorking> dtos) throws ValidationException {
        for (DtoNotWorking dto : dtos) {
            if (dto.menge < 0) {
                throw new ValidationException(String.format("Menge (= %s) für %s darf nicht negativ sein (id: %s)",
                        dto.menge, DtoNotWorking.class.getSimpleName(), dto.id));
            }
        }
    }

    private static class DtoNotWorking {

        public final long id;
        public final int menge;

        public DtoNotWorking(long id, int menge) {
            this.id = id;
            this.menge = menge;
        }

        @Override
        public String toString() {
            return Integer.toHexString(super.hashCode());
        }
    }

    private static class DtoWorking extends DtoNotWorking {

        public DtoWorking(long id, int menge) {
            super(id, menge);
        }

        @Override
        public String toString() {
            return "7212fe80"; // copied from previous run
        }
    }
}
