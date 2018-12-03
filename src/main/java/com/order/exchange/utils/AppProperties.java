package com.order.exchange.utils;

public class AppProperties {

    private String tickerSymbols;

    public AppProperties() {
        tickerSymbols = System.getProperty("tickerSymbols", "GOOGL,NFLX");
    }

    public String[] getTickerSymbols(){
        return tickerSymbols.split(",");
    }
}
