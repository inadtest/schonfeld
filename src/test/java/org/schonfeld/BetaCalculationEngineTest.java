package org.schonfeld;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class BetaCalculationEngineTest {
    //private final BetaCalculationEngine underTest = new BetaCalculationEngine("src/test/resources/TestMarketData.csv");
    private final BetaCalculationEngine underTest = new BetaCalculationEngine("src/test/resources/Test1.csv");

    @Test
    void calculateBetaTest() {
        underTest.calculateBeta("MSFT", "SPY", "8/30/2021", "9/7/2021", 252L);
    }

    @Test
    void calculateAvgTest() {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
                .toFormatter();

        LocalDate sDate = LocalDate.parse("8/31/2021", formatter);
        LocalDate eDate = LocalDate.parse("9/3/2021", formatter);
        System.out.println(underTest.calculateAvg("SPY", sDate, eDate));
        System.out.println(Utils.subtractDaysSkippingWeekends(sDate, 20L));
    }


    @Test
    void calcBetaTest() {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
                .toFormatter();
        LocalDate sDate = LocalDate.parse("9/28/2016", formatter);
        //underTest.calcVarianceForBetaDurationDays("SPY", sDate, 2);
        underTest.calculateBeta("MSFT", "SPY", "9/28/2016", "9/30/2016", 2L);
    }

}
