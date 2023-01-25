package org.schonfeld;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BetaCalculationEngineTest {

    @Test
    void shouldCalculateBetaTest() {
        //given
        BetaCalculationEngine underTest = new BetaCalculationEngine("src/test/resources/Test2.csv");
        String startDate = "9/23/2016";
        String endDate = "9/28/2016";

        //when

        //then
        Double[] vals = underTest.calculateBeta("F", "SPY", startDate, endDate, 3L);
        assertEquals(vals[0], 0.0497);
    }
}
