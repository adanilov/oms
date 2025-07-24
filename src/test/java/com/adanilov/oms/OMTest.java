package com.adanilov.oms;

import com.adanilov.oms.domain.Order;
import com.adanilov.oms.domain.Side;
import com.adanilov.oms.domain.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OMTest {

    OrderMatcher om;

    @BeforeEach
    void beforeEach() {
        om = new OrderMatcher();
    }

    @Test
    void testOneOrderDoesntProduceTrades() {
        List<Trade> trades = om.submitOrder(new Order(1, 2, Side.BUY));
        assertNotNull(trades);
        assertEquals(0, trades.size());
    }

    @Test
    void testTwoSameOrdersOppositeSidesProduceTrade() {
        Order sellOrder = new Order(1, 2, Side.SELL);
        om.submitOrder(sellOrder);

        Order buyOrder = new Order(1, 2, Side.BUY);
        List<Trade> trades = om.submitOrder(buyOrder);
        assertNotNull(trades);
        assertEquals(1, trades.size());
        var trade = trades.get(0);
        assertEquals(1, trade.price);
        assertEquals(2, trade.volume);
        assertEquals(buyOrder.orderId, trade.buyOrderId);
        assertEquals(sellOrder.orderId, trade.sellOrderId);
    }

    @Test
    void testPartialFill() {
        Order sellOrder = new Order(1, 2, Side.SELL);
        om.submitOrder(sellOrder);

        Order buyOrder = new Order(1, 1, Side.BUY);
        List<Trade> trades = om.submitOrder(buyOrder);
        assertNotNull(trades);
        assertEquals(1, trades.size());
        var trade = trades.get(0);
        assertEquals(1, trade.price);
        assertEquals(1, trade.volume);
        assertEquals(buyOrder.orderId, trade.buyOrderId);
        assertEquals(sellOrder.orderId, trade.sellOrderId);
    }

    @Test
    void testAggressiveOrder() {
        Order sellOrder = new Order(1, 2, Side.SELL);
        om.submitOrder(sellOrder);

        Order buyOrder = new Order(10, 2, Side.BUY);
        List<Trade> trades = om.submitOrder(buyOrder);
        assertNotNull(trades);
        assertEquals(1, trades.size());
        var trade = trades.get(0);
        assertEquals(1, trade.price);
        assertEquals(2, trade.volume);
        assertEquals(buyOrder.orderId, trade.buyOrderId);
        assertEquals(sellOrder.orderId, trade.sellOrderId);
    }
}
