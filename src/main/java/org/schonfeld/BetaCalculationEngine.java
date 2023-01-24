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
        LocalDate sDate = LocalDate.parse(startDate, formatter);
        LocalDate eDate = LocalDate.parse(endDate, formatter);
        Double[] beta = new Double[0];
        try {
            beta = HelperUtils.calculateBeta(getStockCache(), ticker, tickerBaseLine, sDate, eDate, betaDurationDays);
            return beta;
        } catch (Exception e) {
            System.out.println("Exception occurred when calculating beta");
            System.out.println(e);
        }
        return beta;
    }

    void process(List<CSVRecord> recs) {
        LocalDate prevDate;
        for (CSVRecord r : recs) {
            if (stockCache.containsKey(String.valueOf(r.get(1)))) {
                TreeMap<LocalDate, MarketData> s = stockCache.get(r.get(1));
                LocalDate date = LocalDate.parse(r.get(0), ofPattern("M/d/yyyy"));
                prevDate = date.minusDays(1L);
                if(!s.containsKey(prevDate)) {
                    while(!s.containsKey(prevDate))
                        prevDate = prevDate.minusDays(1L);
                }
                //LocalDate prevDate = DateUtils.getPreviousWorkingDay(date);
                Double dailyReturn = 0D;

                if (s.containsKey(prevDate)) {
                    MarketData pData = s.get(prevDate);
                    if (pData.getClosePrice() > 0)
                        dailyReturn = calculateDailyReturn(Double.parseDouble(r.get(2)),
                                                            pData.getClosePrice());
                }
                s.put(LocalDate.parse(r.get(0), formatter), new MarketData(Double.valueOf(r.get(2)),
                                                                            dailyReturn));
                stockCache.put(r.get("Ticker"), s);

            } else {
                TreeMap<LocalDate, MarketData> s = new TreeMap<>();
                s.put(LocalDate.parse(r.get(0), formatter), new MarketData(Double.valueOf(r.get("ClosePrice")), 0D));
                stockCache.put(r.get("Ticker"), s);
               // prevDate = LocalDate.parse(r.get(0), formatter);

            }
        }
    }

    private double calculateDailyReturn(Double tPrice, Double yPrice) {
        Double val = 1 + ((tPrice - yPrice) / yPrice);
        return Math.log(val);
    }

}
