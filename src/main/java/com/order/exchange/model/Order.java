package com.order.exchange.model;

public class Order {

    private final String clientRequestId;
    private final String orderId;
    private final String clientName;
    private final String tickerSymbol;
    private final OrderPrice price;
    private final OrderSide side;
    private double size;

    public Order(String clientRequestId, String orderId, String clientName, OrderSide side,
                 String tickerSymbol, double price, double size) {

        this.clientRequestId = clientRequestId;
        this.orderId = orderId;
        this.clientName = clientName;
        this.side = side;
        this.tickerSymbol = tickerSymbol;
        this.price = new OrderPrice(price);
        this.size = size;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public OrderPrice getPrice() {
        return price;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double newSize) {
        this.size = newSize;
    }

    public OrderSide getSide() {
        return side;
    }

    @Override
    public String toString() {
        return "Order{" +
                "clientRequestId='" + clientRequestId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", clientName='" + clientName + '\'' +
                ", tickerSymbol='" + tickerSymbol + '\'' +
                ", price=" + price +
                ", size=" + size +
                ", side=" + side +
                '}';
    }
}
