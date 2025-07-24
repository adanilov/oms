package com.adanilov.exchange.domain;

public class Trade {
    public String buyOrderId;
    public String sellOrderId;
    public int price;
    public int volume;

    public Trade(String buyOrderId, String sellOrderId, int price, int volume) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.volume = volume;
    }
}
