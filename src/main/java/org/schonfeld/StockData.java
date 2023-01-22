package org.schonfeld;

import lombok.Getter;

public class StockData {
    @Getter
    private Double closePrice;
    @Getter
    private Double dailyReturn;
    @Getter
    private Double prefixSum;

    StockData(Double closePrice, Double dailyReturn, Double prefixSum) {
        this.closePrice = closePrice;
        this.dailyReturn = dailyReturn;
        this.prefixSum = prefixSum;
    }
}
