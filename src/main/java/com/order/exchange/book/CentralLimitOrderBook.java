package com.order.exchange.book;

import com.order.exchange.utils.AppProperties;
import com.order.exchange.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CentralLimitOrderBook {

    private static final Logger LOGGER = LoggerFactory.getLogger(CentralLimitOrderBook.class);
    private Map<String, SingleTickerSymbolBook> orderBooksByTickerSymbol;

    /**
     * @param properties - the application properties
     *
     *  CentralLimitOrderBook constructor - contains all of the orderbooks for all shares
     */

    public CentralLimitOrderBook(AppProperties properties) {

        LOGGER.info("Initialising CentralLimitOrderBook");

        this.orderBooksByTickerSymbol = new ConcurrentHashMap<>();
        for (String name : properties.getTickerSymbols()) {
            orderBooksByTickerSymbol.put(name, new SingleTickerSymbolBook(name));
        }
    }

    /**
     *
     * @param tickerSymbol - the tickerSymbol for the book we want to retrieve
     * @return the OrderBook for that tickerSymbol
     */
    public OrderBook getOrderBook(String tickerSymbol) {
        return orderBooksByTickerSymbol.get(tickerSymbol);
    }

    /**
     *
     * @param order - the order to submit throug to the SingleTickerSymbolBook
     */
    public void submitOrder(Order order) {

        LOGGER.info("Order Submitted " + order);

        SingleTickerSymbolBook book = orderBooksByTickerSymbol.get(order.getTickerSymbol());
        book.submitOrder(order);
    }
}
