package com.adanilov.oms.domain;

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

    public Order() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Order{");
        sb.append("orderId='").append(orderId).append('\'');
        sb.append(", price=").append(price);
        sb.append(", volume=").append(volume);
        sb.append(", side=").append(side);
        sb.append('}');
        return sb.toString();
    }
}
