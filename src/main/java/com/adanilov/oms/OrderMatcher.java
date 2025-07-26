package com.adanilov.oms;

import com.adanilov.oms.domain.Order;
import com.adanilov.oms.domain.Side;
import com.adanilov.oms.domain.Trade;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Component
public class OrderMatcher {

    PriorityQueue<Order> buyOrders = new PriorityQueue<>((a, b) -> b.price - a.price);
    PriorityQueue<Order> sellOrders = new PriorityQueue<>((a, b) -> a.price - b.price);

    List<Trade> submitOrder(Order o) {
        if (o.side == Side.BUY) {
            buyOrders.add(o);
        } else {
            sellOrders.add(o);
        }
        List<Trade> result = new ArrayList<>();

        while (!buyOrders.isEmpty() && !sellOrders.isEmpty() && buyOrders.peek().price >= sellOrders.peek().price) {
            Order buyOrder = buyOrders.poll();
            Order sellOrder = sellOrders.poll();
            int price = o.side == Side.BUY ? sellOrder.price : buyOrder.price;
            int volume = Math.min(buyOrder.volume, sellOrder.volume);
            result.add(new Trade(buyOrder.orderId, sellOrder.orderId, price, volume));
            int newBuyOrderVolume = buyOrder.volume - volume;
            int newSellOrderVolume = sellOrder.volume - volume;
            if (newBuyOrderVolume != 0) {
                buyOrders.add(new Order(buyOrder.price, newBuyOrderVolume, Side.BUY));
            }
            if (newSellOrderVolume != 0) {
                sellOrders.add(new Order(sellOrder.price, newSellOrderVolume, Side.SELL));
            }
        }

        return result;
    }
}
