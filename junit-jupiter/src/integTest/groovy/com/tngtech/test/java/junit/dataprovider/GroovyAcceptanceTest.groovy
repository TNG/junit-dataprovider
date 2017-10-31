package com.tngtech.test.java.junit.dataprovider

import com.tngtech.junit.dataprovider.DataProvider
import com.tngtech.junit.dataprovider.DataProviderExtension
import com.tngtech.junit.dataprovider.UseDataProvider
import com.tngtech.junit.dataprovider.UseDataProviderExtension
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith

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

    @TestTemplate
    @ExtendWith(UseDataProviderExtension)
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

    @TestTemplate
    @ExtendWith(UseDataProviderExtension)
    @UseDataProvider('dataProviderBooleanLogicOr')
    void "test boolean logic for 'or'"(op1, op2, expected) {
        // Expect:
        assert (op1 || op2) == expected
    }

    // @formatter:off
    @TestTemplate
    @ExtendWith(DataProviderExtension)
    @DataProvider([
            'false,  false,  false',
            'true,   false,  true ',
            'false,  true,   true ',
            'true,   true,   false'
        ])
    // @formatter:on
    void "test boolean logic for 'xor'"(boolean op1, boolean op2, boolean expected) {
        // Expect:
        def a = [1,2] as String[]
        assert ((op1 || op2) && (op1 != op2)) == expected
    }
}
