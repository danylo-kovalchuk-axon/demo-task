package com.demo.demotask.httpclient;

/**
 * Crypto Price.
 *
 * @author Danylo Kovalchuk
 */
public class CryptoPrice {

    private String symbol;
    private String price;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}