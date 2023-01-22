package org.schonfeld;

import lombok.Getter;
import org.apache.commons.csv.CSVRecord;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.format.DateTimeFormatter.ofPattern;

public class BetaCalculationEngine {
    @Getter
    private final Map<String, SortedMap<LocalDate, StockData>> stockCache;
    private final CsvFileParser cv;
    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
            .toFormatter();


    BetaCalculationEngine(String path ) {
        stockCache = new HashMap<>();
        cv = new CsvFileParser(path);
        process( new CsvFileParser(path).parse());
    }

    //void print()
    public Double calculateBeta(String ticker,
                                String tickerBaseLine,
                                String startDate,
                                String endDate,
                                Long betaDurationDays) {
        LocalDate sDate = LocalDate.parse(startDate, formatter);
        LocalDate eDate = LocalDate.parse(endDate, formatter);
        Long daysLen = Utils.calculateWeekDays(sDate, eDate);
        System.out.println( daysLen);

        return 0D;
    }

    public Double variance() {
        return 0D;
    }



    public Double calculateAvg(String ticker, LocalDate sDate, LocalDate eDate) {
        long days = 0L;
        Double avg = 0D;
        if(stockCache.containsKey(ticker)) {
            SortedMap<LocalDate, StockData> s = stockCache.get(ticker);
            days = Utils.calculateWeekDays(sDate, eDate);
            avg = 0D;
            LocalDate cDate = sDate;
            for(long i = 0; i < days; i++) {
                if(s.containsKey(cDate))
                    avg = s.get(cDate).getClosePrice();
                cDate = Utils.getNextWorkingDay(sDate);
            }
        }
        return days > 0 ? avg/days : 0;
    }


    void process(List<CSVRecord> recs) {
        for(CSVRecord r : recs){
            //System.out.println(r.get("Date"));
            if(stockCache.containsKey(String.valueOf(r.get(1)))) {
                SortedMap<LocalDate, StockData> s = stockCache.get(r.get(1));
                LocalDate date = LocalDate.parse(r.get(0), ofPattern("M/d/yyyy"));
                LocalDate prevDate = Utils.getPreviousWorkingDay(date);
                Double dailyReturn = 0D;
                if(s.containsKey(prevDate)) {
                    StockData pData = s.get(prevDate);
                    if(pData.getClosePrice() > 0)
                        dailyReturn = calculateDailyReturn(Double.parseDouble(r.get(2)), pData.getClosePrice());
                }
                s.put(LocalDate.parse(r.get(0), formatter), new StockData(Double.valueOf(r.get(2)), dailyReturn));

            } else {
                SortedMap<LocalDate, StockData> s = new TreeMap<>();
                s.put(LocalDate.parse(r.get(0), formatter), new StockData(Double.valueOf(r.get("ClosePrice")), 0D));
            }
        }
    }

    double calculateDailyReturn(Double tPrice, Double yPrice) {
        Double val = 1 + (tPrice - yPrice)/yPrice;
        return 6 * (val-1)/(val + 1 + 4 * (Math.sqrt(val)));
    }

    double log(double x) {
        return 6 * (x-1)/(x + 1 + 4 * (Math.sqrt(x)));
    }
}
