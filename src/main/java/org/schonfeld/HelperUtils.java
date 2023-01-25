package org.schonfeld;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HelperUtils {
    private static final DecimalFormat df = new DecimalFormat("0.0000");

    public static Double[] calculateBeta(Map<String, TreeMap<LocalDate, MarketData>> stockMap,
                                         String ticker,
                                         String baselineTicker,
                                         LocalDate startDate,
                                         LocalDate endDate,
                                         long betaDurationDays) throws Exception {
        List<Double> covariance = new ArrayList<>();
        List<Double> variance = new ArrayList<>();
        List<Double> beta = new ArrayList<>();
        int index = 0;
        if(stockMap.containsKey(ticker) && stockMap.containsKey(baselineTicker)) {
            TreeMap<LocalDate, MarketData> marketData = stockMap.get(ticker);
            for(LocalDate date = startDate; date.isBefore(endDate) || date.isEqual(endDate); date = marketData.higherKey(date)) {
                Double tickerAverage = calculateAverage(stockMap, ticker, date, betaDurationDays);
                Double baselineTickerAverage = calculateAverage(stockMap, baselineTicker, date, betaDurationDays);
                covariance.add(calculateCovarianceForBetaDurationDays(stockMap, ticker, baselineTicker, date, betaDurationDays, tickerAverage, baselineTickerAverage));
                variance.add(calcVarianceForBetaDurationDays(stockMap, baselineTicker, date, betaDurationDays, baselineTickerAverage));
                beta.add(Double.valueOf(df.format(covariance.get(index)/variance.get(index))));
                index++;
            }
        } else {
            throw new Exception("ticker or baselineTicker not found in the input data");
        }
        Double[] arr = new Double[beta.size()];
        arr = beta.toArray(arr);
        return arr;
    }

    public static Double calculateCovarianceForBetaDurationDays(Map<String, TreeMap<LocalDate, MarketData>> stockMap,
                                                                String ticker,
                                                                String baselineTicker,
                                                                LocalDate date,
                                                                long betaDurationDays,
                                                                double tickerAverage,
                                                                double baselineTickerAverage) {
        long sampleSize = 0L;
        double tickerDailyReturn;
        double tickerBaselineDailyReturn;
        double covariance = 0D;
        LocalDate currentDate = date;
        TreeMap<LocalDate, MarketData> tickerMap = stockMap.get(ticker);
        TreeMap<LocalDate, MarketData> baselineTickerMap = stockMap.get(baselineTicker);

        while(currentDate != null && betaDurationDays > 0 && sampleSize <= betaDurationDays) {
            MarketData tickerData = tickerMap.get(currentDate);
            MarketData baselineTickerData = baselineTickerMap.get(currentDate);
            tickerDailyReturn = tickerData.getDailyReturn();
            tickerBaselineDailyReturn = baselineTickerData.getDailyReturn();
            covariance += (tickerDailyReturn - tickerAverage) * (tickerBaselineDailyReturn - baselineTickerAverage);
            sampleSize++;
            currentDate = tickerMap.lowerKey(currentDate);
        }
        return (sampleSize == 1) ? covariance : covariance/(sampleSize - 1);
    }


    public static Double calcVarianceForBetaDurationDays(Map<String, TreeMap<LocalDate, MarketData>> stockMap,
                                                         String ticker,
                                                         LocalDate date,
                                                         long betaDurationDays,
                                                         double baselineTickerAverage) {
        long sampleSize = 1L;
        double variance = 0D;
        double dReturn;
        LocalDate cDate = date;
        TreeMap<LocalDate, MarketData> sMap = stockMap.get(ticker);

        while(cDate != null && betaDurationDays > 0 && sampleSize <= betaDurationDays) {
            if(sMap.containsKey(cDate)) {
                MarketData sData = sMap.get(cDate);
                dReturn = sData.getDailyReturn();
                variance += (dReturn - baselineTickerAverage) * (dReturn - baselineTickerAverage);
            }
            sampleSize++;
            cDate = sMap.lowerKey(cDate);
        }
        return (sampleSize == 1) ? variance : variance/(sampleSize - 1);
    }

    public static Double calculateAverage(Map<String, TreeMap<LocalDate, MarketData>> tickerMap,
                                          String ticker,
                                          LocalDate date,
                                          long betaDurationDays) {
        LocalDate currentDate = date;
        long sampleSize = 0L;
        Double tickerSum = 0D;
        TreeMap<LocalDate, MarketData> stockData = tickerMap.get(ticker);

        while(currentDate != null && betaDurationDays > 0 && sampleSize <= betaDurationDays) {
            MarketData tickerData = stockData.get(currentDate);
            tickerSum += tickerData.getClosePrice();
            sampleSize++;
            currentDate = stockData.lowerKey(currentDate);
        }
        return sampleSize > 0? tickerSum/sampleSize : 0;
    }
}
