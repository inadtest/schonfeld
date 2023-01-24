package org.schonfeld;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MarketDataTest {

    @Test
    void shouldCreateMarketData() {
        //given
        BetaCalculationEngine underTest = new BetaCalculationEngine("src/test/resources/TestMarketData.csv");
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
                .toFormatter();
        LocalDate sDate = LocalDate.parse("9/28/2016", formatter);

        //when
        TreeMap<LocalDate, MarketData> marketData = underTest.getStockCache().get("SPY");

        //then
        assertEquals(marketData.get(sDate).getClosePrice(), 197.807251);
    }
}
