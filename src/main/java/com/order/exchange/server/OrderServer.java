package com.order.exchange.server;

import com.order.exchange.book.CentralLimitOrderBook;
import com.order.exchange.book.OrderBook;
import com.order.exchange.utils.AppProperties;
import com.order.exchange.model.Order;
import com.order.exchange.model.OrderRequest;
import com.order.exchange.idgen.OrderIdGenerator;


public class OrderServer {

    private final OrderIdGenerator gen;
    private final CentralLimitOrderBook centralLimitOrderBook;

    public OrderServer(AppProperties properties) {
        this.centralLimitOrderBook = new CentralLimitOrderBook(properties);
        this.gen = new OrderIdGenerator();
    }

    public void placeOrder(OrderRequest request) {

        centralLimitOrderBook.submitOrder(new Order(request.getClientRequestId(), gen.generateOrderId(),
                request.getClientName(), request.getSide(),
                request.getTickerSymbol(), request.getPrice(), request.getSize()));
    }

    public OrderBook getBook(String tickerSymbol){
        return centralLimitOrderBook.getOrderBook(tickerSymbol);

    }


}




