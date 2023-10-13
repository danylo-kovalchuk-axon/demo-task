package com.demo.demotask.service;

import com.demo.demotask.httpclient.CryptoPrice;
import com.demo.demotask.httpclient.CryptoPricesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Crypto Price Service.
 *
 * @author Danylo Kovalchuk
 */
@Service
public class CryptoPriceService {

    @Autowired
    private CryptoPricesClient cryptoPricesClient;

    /**
     * Gets current crypto prices.
     *
     * @return current crypto prices
     */
    public List<CryptoPrice> getCurrentCryptoPrices() {
        return cryptoPricesClient.getCurrentCryptoPrices();
    }
}