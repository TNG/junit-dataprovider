package com.tngtech.test.java.junit.dataprovider

import com.tngtech.junit.dataprovider.DataProvider
import com.tngtech.junit.dataprovider.UseDataProvider
import org.junit.jupiter.params.ParameterizedTest

class GroovyAcceptanceTest {

    @DataProvider
    static List<List<Object>> dataProviderBooleanLogicAnd() {
        // @formatter:off
        return [
            [ false,  false,  false ],
            [ true,   false,  false ],
            [ false,  true,   false ],
            [ true,   true,   true ],
        ]
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider('dataProviderBooleanLogicAnd')
    void "test boolean logic for 'and'"(op1, op2, expected) {
        // Expect:
        assert (op1 && op2) == expected
    }

    @DataProvider
    static List<List<Object>> dataProviderBooleanLogicOr() {
        // @formatter:off
        return [
                [ false,  false,  false ],
                [ true,   false,  true ],
                [ false,  true,   true ],
                [ true,   true,   true ],
                ]
        // @formatter:on
    }

    @ParameterizedTest
    @UseDataProvider('dataProviderBooleanLogicOr')
    void "test boolean logic for 'or'"(op1, op2, expected) {
        // Expect:
        assert (op1 || op2) == expected
    }

    // @formatter:off
    @ParameterizedTest
    @DataProvider([
            'false,  false,  false',
            'true,   false,  true ',
            'false,  true,   true ',
            'true,   true,   false'
        ])
    // @formatter:on
    void "test boolean logic for 'xor'"(boolean op1, boolean op2, boolean expected) {
        // Expect:
        assert ((op1 || op2) && (op1 != op2)) == expected
    }
}
