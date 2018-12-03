package com.order.exchange.model;

public class OrderPrice {

    private final double price;

    public OrderPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderPrice that = (OrderPrice) o;

        return Double.compare(that.price, price) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(price);
        return (int) (temp ^ (temp >>> 32));
    }


    @Override
    public String toString() {
        return "OrderPrice{" +
                "price=" + price +
                '}';
    }
}
