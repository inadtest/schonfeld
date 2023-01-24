package org.schonfeld;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HelperUtils {
    private static final DecimalFormat df = new DecimalFormat("0.0000");

    public static Double[] calculateBeta(Map<String, TreeMap<LocalDate, MarketData>> stockMap, String ticker, String indexTicker, LocalDate sDate, LocalDate eDate, long betaDays) throws Exception {
        List<Double> covariance = new ArrayList<>();
        List<Double> variance = new ArrayList<>();
        List<Double> beta = new ArrayList<>();
        int index = 0;
        if(stockMap.containsKey(ticker) && stockMap.containsKey(indexTicker)) {
            for(LocalDate date = sDate; date.isBefore(eDate) || date.isEqual(eDate); date = date.plusDays(1L)) {
                if(stockMap.containsKey(ticker) && stockMap.containsKey(indexTicker)) {
                    covariance.add(calculateCovarianceForBetaDurationDays(stockMap, ticker, indexTicker, date, betaDays));
                    variance.add(calcVarianceForBetaDurationDays(stockMap, indexTicker, date, betaDays));
                    beta.add(Double.valueOf(df.format(covariance.get(index)/variance.get(index))));
                    index++;
                } else {
                    throw new Exception("ticker or indexticker not found in the input data");
                }
            }
        }
        Double[] arr = new Double[beta.size()];
        arr = beta.toArray(arr);
        return arr;
    }

    public static Double calculateCovarianceForBetaDurationDays(Map<String, TreeMap<LocalDate, MarketData>> stockMap, String ticker, String indexTicker, LocalDate date, long betaDurationDays) {
            long betaDays = betaDurationDays;
            long index = 1L;
            Double tickerAvg;
            Double idxTickerAvg;
            Double tickerDailyReturn;
            Double indexDailyReturn;
            Double tickerSum = 0D;
            Double indexTickerSum = 0D;
            Double covariance = 0D;

            LocalDate cDate = date;
            TreeMap<LocalDate, MarketData> tickerMap = stockMap.get(ticker);
            TreeMap<LocalDate, MarketData> idxTickerMap = stockMap.get(indexTicker);

            while(betaDays > 0 && index <= betaDays) {
                if(tickerMap.containsKey(cDate) && idxTickerMap.containsKey(cDate)) {
                    MarketData tickerData = tickerMap.get(cDate);
                    MarketData indexData = idxTickerMap.get(cDate);
                    tickerDailyReturn = tickerData.getDailyReturn();
                    indexDailyReturn = indexData.getDailyReturn();
                    tickerSum += tickerData.getClosePrice();
                    tickerAvg = tickerSum/index;
                    indexTickerSum += indexData.getClosePrice();
                    idxTickerAvg = indexTickerSum/index;
                    covariance += (tickerDailyReturn - tickerAvg) * (indexDailyReturn - idxTickerAvg);

                } else if(!tickerMap.containsKey(cDate) && cDate.isAfter(tickerMap.firstKey())) {
                    // skips if the date before is not in the map
                    while(!tickerMap.containsKey(cDate) && cDate.isAfter(tickerMap.firstKey())) {
                        cDate = cDate.minusDays(1L);

                    }
                    continue;
                } else if(cDate.isBefore(tickerMap.firstKey())) {
                    // decreasing by 2 because index was already incremented but the date is not in the map
                    if(index > 2)
                        return covariance / (index - 2);
                    else {
                        if(index == 0 || index == 1 || index == 2)
                            return covariance;
                    }
                }
                index++;
                cDate = cDate.minusDays(1L);
            }

        if (index == 0 || index == 1 || index == 2)
            return covariance;
        else
            return covariance/(index-2);

    }


    public static Double calcVarianceForBetaDurationDays(Map<String, TreeMap<LocalDate, MarketData>> stockMap, String ticker, LocalDate date, long betaDurationDays) {
        long betaDays = betaDurationDays;
        long index = 1L;
        Double avg;
        Double sum = 0D;
        Double variance = 0D;
        Double dReturn;
        LocalDate cDate = date;
        TreeMap<LocalDate, MarketData> sMap = stockMap.get(ticker);

        while(betaDays > 0 && index <= betaDays) {
            if(sMap.containsKey(cDate)) {
                MarketData sData = sMap.get(cDate);
                dReturn = sData.getDailyReturn();
                sum += sData.getClosePrice();
                avg = sum/index;
                variance += (dReturn - avg) * (dReturn - avg);

            } else if(!sMap.containsKey(cDate) && cDate.isAfter(sMap.firstKey())) {
                // skips if the date before is not in the map
                while(!sMap.containsKey(cDate) && cDate.isAfter(sMap.firstKey()) )
                    cDate = cDate.minusDays(1L);
                continue;
            } else if(cDate.isBefore(sMap.firstKey())) {
                if (index == 0 || index == 1 || index == 2)
                    return variance;
                else
                    return variance/(index-2);
            }
            index++;
            cDate = cDate.minusDays(1L);
        }
        if (index == 0 || index == 1 || index == 2)
            return variance;
        else
            return variance/(index-2);

    }
}
