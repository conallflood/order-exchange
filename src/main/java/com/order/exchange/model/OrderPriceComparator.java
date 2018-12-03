package com.order.exchange.model;

import java.util.Comparator;

public class OrderPriceComparator implements Comparator<OrderPrice> {

    private final OrderSide side;

    public OrderPriceComparator(OrderSide side) {
        this.side = side;
    }

    public int compare(OrderPrice o1, OrderPrice o2) {

        if (o1.getPrice() < o2.getPrice())
            return side == OrderSide.BID ? 1 : -1;

        else if (o1.getPrice() > o2.getPrice()) {
            return side == OrderSide.BID ? -1 : 1;

        } else
            return 0;
    }
}
