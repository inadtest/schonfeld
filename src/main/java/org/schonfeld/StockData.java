package org.schonfeld;

import lombok.Getter;

public class StockData {
    @Getter
    private Double closePrice;
    private Double dailyReturn;

    StockData(Double closePrice, Double dailyReturn) {
        this.closePrice = closePrice;
        this.dailyReturn = dailyReturn;
    }
}
