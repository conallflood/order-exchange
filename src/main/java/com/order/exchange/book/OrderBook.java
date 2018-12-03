package com.order.exchange.book;

import com.order.exchange.model.OrderPrice;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public interface OrderBook {

    /**
     * @return  - all bid orders in the book
     */
    ConcurrentMap<OrderPrice, List> getAllBids();

    /**
     * @return  - all offer orders in the book
     */
    ConcurrentMap<OrderPrice, List> getAllOffers();

    /**
     * @return the top of the book - the best bid and ask orders in the book
     */
    List[] getTopOfBook();
}
