package com.order.exchange.model;

public class OrderRequest {

    private final String clientRequestId;
    private final String clientName;
    private final OrderSide side;
    private final String tickerSymbol;
    private final double price;
    private final double size;

    public OrderRequest(String clientRequestId, String clientName, OrderSide side,
                        String tickerSymbol, double price, double size) {

        this.clientRequestId = clientRequestId;
        this.clientName = clientName;
        this.side = side;
        this.tickerSymbol = tickerSymbol;
        this.price = price;
        this.size = size;
    }


    public String getClientRequestId() {
        return clientRequestId;
    }

    public String getClientName() {
        return clientName;
    }

    public OrderSide getSide() {
        return side;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public double getPrice() {
        return price;
    }

    public double getSize() {
        return size;
    }
}
