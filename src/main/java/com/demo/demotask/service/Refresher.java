package com.demo.demotask.service;

import com.demo.demotask.httpclient.CryptoPrice;
import com.demo.demotask.util.BigDecimalRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

/**
 * Crypto Currency Notifier.
 *
 * @author Danylo Kovalchuk
 */
@Service
public class Refresher {

    private static final Logger LOG = LoggerFactory.getLogger(Refresher.class);

    @Autowired
    private BotSubscriberService botSubscriberService;

    @Autowired
    private CryptoPriceService cryptoPriceService;

    @Autowired
    private BotSubscriberNotificationService botSubscriberNotificationService;

    @Scheduled(fixedDelayString = "${demo.delay-seconds}", timeUnit = TimeUnit.SECONDS)
    private void runUpdate() {
        LOG.debug("Starting update...");
        var cachedNotifyRangesByCurrency = botSubscriberService.getCachedRangeToNotNotifyAboutByChatId();
        if (cachedNotifyRangesByCurrency.isEmpty()) {
            LOG.debug("No subscribers to notify...");
            return;
        }

        var currentCryptoPrices = getCryptocurrencyPrices();
        if (currentCryptoPrices.isEmpty()) {
            LOG.debug("Crypto prices weren't updated - no need to try notify subscribers...");
            return;
        }

        notifySubscribers(currentCryptoPrices, cachedNotifyRangesByCurrency);
        LOG.debug("Finishing subscriber notification...");
    }

    /* Private methods */

    private Map<String, BigDecimal> getCryptocurrencyPrices() {
        try {
            return cryptoPriceService
                    .getCurrentCryptoPrices()
                    .stream()
                    .collect(toMap(CryptoPrice::getSymbol, cryptoPrice -> new BigDecimal(cryptoPrice.getPrice())));
        } catch (Exception e) {
            LOG.debug("Exception occurred on call to external api: {}", e.getMessage());
            return emptyMap();
        }
    }

    private void notifySubscribers(Map<String, BigDecimal> currentCryptoPrices,
                                   Map<Long, Map<String, BigDecimalRange>> cachedPricesByChatId) {

        for (var entry : cachedPricesByChatId.entrySet()) {
            botSubscriberNotificationService.notifySubscriber(entry.getKey(), currentCryptoPrices, Map.copyOf(entry.getValue()));
        }
    }
}