package com.order.exchange.client;

import com.order.exchange.model.OrderRequest;

public interface OrderClient {

    void placeOrder(OrderRequest order);


}
