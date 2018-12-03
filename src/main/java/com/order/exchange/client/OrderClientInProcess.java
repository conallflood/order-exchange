package com.order.exchange.client;

import com.order.exchange.model.OrderRequest;
import com.order.exchange.server.OrderServer;

public class OrderClientInProcess implements OrderClient {

    private OrderServer server;

    /**
     * @param server - the order server
     *
     * In the real world this would be a remote connection but for this exercise
     *  we cna do in process
     */

    public OrderClientInProcess(OrderServer server) {
        this.server = server;
    }

    /**
     * @param request - the order request to send to the server
     *
     * Will place an ordeer request starigh tthrough to the order server
     */


    @Override
    public void placeOrder(OrderRequest request) {
        server.placeOrder(request);
    }


}
