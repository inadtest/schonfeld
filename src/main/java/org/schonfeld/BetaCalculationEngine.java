package org.schonfeld;


import lombok.Getter;
import org.apache.commons.csv.CSVRecord;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

import static java.time.format.DateTimeFormatter.ofPattern;

public class BetaCalculationEngine {
    @Getter
    private static Map<String, TreeMap<LocalDate, MarketData>> stockCache;
    private final CsvFileParser cv;
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                                                                .appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
                                                                .toFormatter();


    BetaCalculationEngine(String path) {
        stockCache = new HashMap<>();
        cv = new CsvFileParser(path);
        // read the input file and load the data in the cache
        process(new CsvFileParser(path).parse());
        stockCache = Collections.unmodifiableMap(stockCache);
    }

    public Map<String, TreeMap<LocalDate, MarketData>> getStockCache() {
        return stockCache;
    }

    public Double[] calculateBeta(String ticker,
                                  String tickerBaseLine,
                                  String startDate,
                                  String endDate,
                                  Long betaDurationDays) {
        LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
        LocalDate endLocalDate = LocalDate.parse(endDate, formatter);
        Double[] beta = new Double[0];
        try {
            beta = HelperUtils.calculateBeta(getStockCache(), ticker, tickerBaseLine, startLocalDate, endLocalDate, betaDurationDays);
            return beta;
        } catch (Exception e) {
            System.out.println("Exception occurred when calculating beta");
            System.out.println(e);
        }
        return beta;
    }

    void process(List<CSVRecord> csvRecords) {
        for (CSVRecord csvRecord : csvRecords) {
            if (stockCache.containsKey(String.valueOf(csvRecord.get("Ticker")))) {
                TreeMap<LocalDate, MarketData> marketData = stockCache.get(csvRecord.get("Ticker"));
                LocalDate date = LocalDate.parse(csvRecord.get("Date"), ofPattern("M/d/yyyy"));
                LocalDate previousDate = marketData.lowerKey(date);
                Double dailyReturn = 0D;

                if (previousDate != null) {
                    MarketData previousDateMarketData = marketData.get(previousDate);
                    if (previousDateMarketData.getClosePrice() > 0)
                        dailyReturn = calculateDailyReturn(Double.parseDouble(csvRecord.get("ClosePrice")),
                                                            previousDateMarketData.getClosePrice());
                }
                marketData.put(LocalDate.parse(csvRecord.get("Date"), formatter), new MarketData(Double.valueOf(csvRecord.get("ClosePrice")),
                                                                            dailyReturn));
                stockCache.put(csvRecord.get("Ticker"), marketData);

            } else {
                TreeMap<LocalDate, MarketData> tempMarketData = new TreeMap<>();
                tempMarketData.put(LocalDate.parse(csvRecord.get("Date"), formatter), new MarketData(Double.valueOf(csvRecord.get("ClosePrice")), 0D));
                stockCache.put(csvRecord.get("Ticker"), tempMarketData);
            }
        }
    }

    private double calculateDailyReturn(Double todayPrice, Double yesterdayPrice) {
        Double value = 1 + ((todayPrice - yesterdayPrice) / yesterdayPrice);
        return Math.log(value);
    }

}
