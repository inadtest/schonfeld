package org.schonfeld;

import lombok.Getter;

public class MarketData {
    @Getter
    private final Double closePrice;
    @Getter
    private final Double dailyReturn;
//    @Getter
//    private Double prefixSum;

    MarketData(Double closePrice, Double dailyReturn) {
        this.closePrice = closePrice;
        this.dailyReturn = dailyReturn;
        //this.prefixSum = prefixSum;
    }
}
