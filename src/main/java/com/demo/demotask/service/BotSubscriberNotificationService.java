package com.demo.demotask.service;

import com.demo.demotask.util.BigDecimalRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bot Subscriber Processor.
 *
 * @author Danylo Kovalchuk
 */
@Service
public class BotSubscriberNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(BotSubscriberNotificationService.class);
    private static final int MAX_TELEGRAM_MESSAGE_LENGTH = 4095;

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private BotSubscriberService botSubscriberService;

    /**
     * Notify user about currency change, if there was change.
     *
     * @param chatId                           id of subscriber's chat
     * @param latestCryptocurrencyPrices       latest cryptocurrency prices
     * @param subscriberRangesToNotNotifyAbout ranges of subscriber, based on which will be decided to notify user or not
     */
    @Async
    public void notifySubscriber(Long chatId,
                                 Map<String, BigDecimal> latestCryptocurrencyPrices,
                                 Map<String, BigDecimalRange> subscriberRangesToNotNotifyAbout) {

        if (latestCryptocurrencyPrices.isEmpty() || subscriberRangesToNotNotifyAbout.isEmpty()) {
            return;
        }

        var changedCurrency = getChangedCurrency(latestCryptocurrencyPrices, subscriberRangesToNotNotifyAbout);
        if (!changedCurrency.isEmpty()) {
            botSubscriberService.updateSubscriberRangeCache(chatId, changedCurrency);
            notifyUserAboutCurrencyChange(chatId, changedCurrency);
        }
    }

    /* Private methods */

    private Map<String, BigDecimal> getChangedCurrency(Map<String, BigDecimal> latestCryptocurrencyPrices,
                                                       Map<String, BigDecimalRange> subscriberPriceRanges) {

        var currencyUpdateNotification = new HashMap<String, BigDecimal>();
        for (var entry : subscriberPriceRanges.entrySet()) {
            var currencyName = entry.getKey();
            var latestCurrencyPrice = latestCryptocurrencyPrices.get(currencyName);

            var rangeToNotNotifyAbout = subscriberPriceRanges.get(currencyName);
            if (rangeToNotNotifyAbout != null && rangeToNotNotifyAbout.notContainsExclusive(latestCurrencyPrice)) {
                currencyUpdateNotification.put(currencyName, latestCurrencyPrice);
            }
        }

        return currencyUpdateNotification;
    }

    private void notifyUserAboutCurrencyChange(Long chatId, Map<String, BigDecimal> changedCurrency) {
        sendNotifications(chatId, generateMessagesAboutCurrencyChange(changedCurrency));
    }

    private void sendNotifications(Long chatId, List<String> messages) {
        for (var message : messages) {
            try {
                sendNotification(chatId, message);
            } catch (TelegramApiException e) {
                LOG.warn("Notification send failed due to: " + e.getMessage());
                botSubscriberService.removeBotSubscriber(chatId);
                break;
            }
        }
    }

    private void sendNotification(Long chatId, String message) throws TelegramApiException {
        telegramBot.execute(new SendMessage(chatId.toString(), message));
    }

    private List<String> generateMessagesAboutCurrencyChange(Map<String, BigDecimal> changedCurrency) {
        var messages = new ArrayList<String>();
        var message = new StringBuilder();
        for (var entry : changedCurrency.entrySet()) {
            var part = generatePartOfMessageAboutCurrencyChange(entry.getKey(), entry.getValue());
            if (message.length() + part.length() > MAX_TELEGRAM_MESSAGE_LENGTH) {
                messages.add(message.toString());
                message = new StringBuilder();
            }

            message.append(part);
        }

        if (message.length() > 0) {
            messages.add(message.toString());
        }

        return messages;
    }

    private String generatePartOfMessageAboutCurrencyChange(String currencyName, BigDecimal currencyPrice) {
        return "Currency Name: " + currencyName + ", currency price: " + currencyPrice.toPlainString() + ";\n";
    }
}