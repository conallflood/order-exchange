package com.order.exchange.idgen;


import java.util.concurrent.atomic.AtomicLong;

public class OrderIdGenerator {

    private final AtomicLong counter;

    public OrderIdGenerator(){
        this.counter = new AtomicLong();
    }

    public String generateOrderId(){
        // just for this exercide will use an AtomicLOng
        // but in the real world this would need to be properly unique across JVM restarts
        // and multiple instances etc
        return String.valueOf(counter.incrementAndGet());
    }
}
