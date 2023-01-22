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
import static java.time.format.DateTimeFormatter.parsedExcessDays;

public class BetaCalculationEngine {
    @Getter
    private final Map<String, SortedMap<LocalDate, StockData>> stockCache;
    @Getter
    private Map<String, SortedMap<LocalDate, StockData>> immutableMap;
    private final CsvFileParser cv;
    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
            .toFormatter();


    BetaCalculationEngine(String path) {
        stockCache = new HashMap<>();
        cv = new CsvFileParser(path);
        process( new CsvFileParser(path).parse());
        immutableMap = Collections.unmodifiableMap(stockCache);
    }

    void print() {
//        for(Map<> map : stockCache.containsKey()) {
//
//        }
    }

    public Double[] calculateBeta(String ticker,
                                String tickerBaseLine,
                                String startDate,
                                String endDate,
                                Long betaDurationDays) {
        LocalDate sDate = LocalDate.parse(startDate, formatter);
        LocalDate eDate = LocalDate.parse(endDate, formatter);
        Long daysLen = Utils.calculateWeekDays(sDate, eDate);
        return calculateCoVarianceAndVariance(ticker, tickerBaseLine, sDate, eDate, betaDurationDays);
    }

    public Double[] calculateCoVarianceAndVariance(String ticker, String indexTicker, LocalDate sDate, LocalDate eDate, long betaDays) {
        Long days = Utils.calculateWeekDays(sDate, eDate);
        Double[] coVariance = new Double[Math.toIntExact(days)];
        Double[] varianceIndexTicker = new Double[Math.toIntExact(days)];
        Double[] varianceTicker = new Double[Math.toIntExact(days)];
        Double[] beta = new Double[Math.toIntExact(days)];

        int idx = 0;
        if(immutableMap.containsKey(ticker) && immutableMap.containsKey(indexTicker)) {
            Map<LocalDate, StockData> tickerMap = immutableMap.get(ticker);
            Map<LocalDate, StockData> indexMap = immutableMap.get(indexTicker);
            for(LocalDate date = sDate; date.isBefore(eDate); date = Utils.getNextWorkingDay(date)) {
                LocalDate firstDate = Utils.subtractDaysSkippingWeekends(date, betaDays);
                if(tickerMap != null && indexMap != null && tickerMap.containsKey(firstDate) && indexMap.containsKey(firstDate) ) {
                    varianceIndexTicker[idx] = calcVarianceForBetaDurationDays(indexTicker, date, betaDays);
                    varianceTicker[idx] = calcVarianceForBetaDurationDays(ticker, date, betaDays);
                    coVariance[idx] = ( varianceIndexTicker[idx] * Math.sqrt( varianceTicker[idx] ) )/(betaDays - 1);
                    varianceIndexTicker[idx] = varianceIndexTicker[idx] /(betaDays - 1);
                    beta[idx] = coVariance[idx]/varianceIndexTicker[idx];
                    idx++;
                }
            }
        }
        return beta;
    }


    public Double[] calculateVariance(String ticker, LocalDate sDate, LocalDate eDate, long betaDays) {
        Long days = Utils.calculateWeekDays(sDate, eDate);
        Double[] variance = new Double[Math.toIntExact(days)];
        Map<LocalDate, StockData> s = immutableMap.get(ticker);
        int idx = 0;
        if(immutableMap.containsKey(ticker)) {
            for(LocalDate date = sDate; date.isBefore(eDate); date = Utils.getNextWorkingDay(date)) {
                LocalDate firstDate = Utils.subtractDaysSkippingWeekends(date, betaDays);
                if(s.containsKey(firstDate)) {
                    variance[idx++] = calcVarianceForBetaDurationDays(ticker, date, betaDays);
                }
            }
        }
        return variance;
    }

    public Double calcVarianceForBetaDurationDays(String ticker, LocalDate date, long betaDurationDays) {
        long betaDays = betaDurationDays;
        Double avg;
        LocalDate firstDate = Utils.subtractDaysSkippingWeekends(date, betaDays); // 9/27
        LocalDate prevDate = Utils.getPreviousWorkingDay(firstDate); // 9/26
        Map<LocalDate, StockData> sMap = immutableMap.get(ticker);
        Double prevPrefixSum = 0D;
        if(sMap.containsKey(prevDate)) {
            StockData sData = sMap.get(prevDate);
            prevPrefixSum = sData.getPrefixSum();
        }
        Double prefixSum = 0D;
        if(sMap.containsKey(date)) {
            StockData sData = sMap.get(date);
            prefixSum = sData.getPrefixSum();
        }
        Double variance = 0D;
        for(LocalDate dt = firstDate; dt.isBefore(date); dt = Utils.getNextWorkingDay(dt)) {
            if(betaDays >= 1) {
                avg = (prefixSum - prevPrefixSum) / betaDays;
                Double dReturn = 0D;
                if (sMap.containsKey(dt)) {
                    StockData sData = sMap.get(dt);
                    dReturn = sData.getDailyReturn();
                }
                variance += (dReturn - avg) * (dReturn - avg);
                betaDays = betaDays - 1;
                prevPrefixSum = sMap.get(dt).getPrefixSum();
            }
        }
        return variance;//variance/(betaDurationDays - 1);
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
            if(stockCache.containsKey(String.valueOf(r.get(1)))) {
                SortedMap<LocalDate, StockData> s = stockCache.get(r.get(1));
                LocalDate date = LocalDate.parse(r.get(0), ofPattern("M/d/yyyy"));
                LocalDate prevDate = Utils.getPreviousWorkingDay(date);
                Double dailyReturn = 0D;
                Double prefixSum = 0D;
                if(s.containsKey(prevDate)) {
                    StockData pData = s.get(prevDate);
                    prefixSum = pData.getPrefixSum() + Double.parseDouble(r.get(2));
                    if(pData.getClosePrice() > 0)
                        dailyReturn = calculateDailyReturn(Double.parseDouble(r.get(2)), pData.getClosePrice());
                }
                s.put(LocalDate.parse(r.get(0), formatter), new StockData(Double.valueOf(r.get(2)), dailyReturn, prefixSum));
                stockCache.put(r.get("Ticker"), s);

            } else {
                SortedMap<LocalDate, StockData> s = new TreeMap<>();
                s.put(LocalDate.parse(r.get(0), formatter), new StockData(Double.valueOf(r.get("ClosePrice")), 0D, Double.valueOf(r.get("ClosePrice"))));
                stockCache.put(r.get("Ticker"), s);
            }
        }
    }

    double calculateDailyReturn(Double tPrice, Double yPrice) {
        Double val = 1 + (tPrice - yPrice)/yPrice;
        return Math.log(val);
    }
}
