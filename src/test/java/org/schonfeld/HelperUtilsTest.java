package org.schonfeld;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelperUtilsTest {
    private static BetaCalculationEngine underTest = new BetaCalculationEngine("src/test/resources/Test2.csv");
    private static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                                                .appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
                                                .toFormatter();
    @Test
    void shouldCalculateCovarianceForBetaDurationDays() {
        //given
        LocalDate date = LocalDate.parse("9/28/2016", formatter);

        //when
        Map<String, TreeMap<LocalDate, MarketData>> cache = underTest.getStockCache();

        //then
        assertEquals(HelperUtils.calculateCovarianceForBetaDurationDays(cache, "F", "SPY",date,5L), 2550.216731509954);
    }

    @Test
    void shouldCalculateVarianceForBetaDurationDays() {
        //given
        LocalDate date = LocalDate.parse("9/28/2016", formatter);

        //when
        Map<String, TreeMap<LocalDate, MarketData>> cache = underTest.getStockCache();

        //then
        assertEquals(HelperUtils.calcVarianceForBetaDurationDays(cache, "SPY",date,5L), 51842.9031740696);
    }

}
