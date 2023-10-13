package com.demo.demotask.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Crypto Prices Client.
 *
 * @author Danylo Kovalchuk
 */
@FeignClient(name = "crypto-prices", url = "https://api.mexc.com/api/v3/ticker/price")
public interface CryptoPricesClient {

    @GetMapping
    List<CryptoPrice> getCurrentCryptoPrices();
}