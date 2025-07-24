package com.adanilov.exchange.domain;

import java.util.UUID;

public class Order {
    public String orderId;
    public int price;
    public int volume;
    public Side side;

    public Order(int price, int volume, Side side) {
        this.orderId = UUID.randomUUID().toString();
        this.price = price;
        this.volume = volume;
        this.side = side;
    }
}
