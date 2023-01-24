package org.schonfeld;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BetaCalculationEngineTest {
    private final BetaCalculationEngine underTest = new BetaCalculationEngine("src/test/resources/Test2.csv");
    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
            .toFormatter();

    @Test
    void shouldCalculateBetaTest() throws Exception {
        //given
        LocalDate date = LocalDate.parse("9/28/2016", formatter);

        //when

        //then
        Double[] vals = underTest.calculateBeta("F", "SPY", "9/23/2016", "9/27/2016", 3L);
        assertEquals(vals[0], 0.0497);
    }
}
