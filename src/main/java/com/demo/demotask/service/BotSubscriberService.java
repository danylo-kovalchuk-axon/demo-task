package com.demo.demotask.service;

import com.demo.demotask.config.DemoConfigProperties;
import com.demo.demotask.domain.model.BotSubscriber;
import com.demo.demotask.domain.repository.BotSubscriberRepository;
import com.demo.demotask.util.BigDecimalRange;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toConcurrentMap;

/**
 * Chats.
 *
 * @author Danylo Kovalchuk
 */
@Component
public class BotSubscriberService {

    private static final Logger LOG = LoggerFactory.getLogger(BotSubscriberService.class);
    private final Map<Long, Map<String, BigDecimalRange>> cachedRangeToNotNotifyAboutByChatId = new ConcurrentHashMap<>();

    @Autowired
    private DemoConfigProperties properties;

    @Autowired
    private BotSubscriberRepository botSubscriberRepository;

    @Autowired
    private CryptoPriceService cryptoPriceService;

    @PostConstruct
    private void init() {
        refreshCache();
    }

    /**
     * Create new bot subscriber.
     *
     * @param botSubscriber bot subscriber to create
     */
    public void createBotSubscriber(BotSubscriber botSubscriber) {
        if (cachedRangeToNotNotifyAboutByChatId.containsKey(botSubscriber.getChatId())) {
            return;
        }

        setupBotSubscriber(botSubscriber);
    }

    /**
     * Set subscriber initial prices to current crypto prices.
     *
     * @param chatId id of the subscriber's chat
     */
    public void refreshSubscriberInitialPrices(Long chatId) {
        var botSubscriberToRefresh = botSubscriberRepository.getBotSubscriberByChatId(chatId);
        setupBotSubscriber(botSubscriberToRefresh);
    }

    /**
     * Updates subscribers cached range values.
     *
     * @param chatId id of the subscriber's chat
     * @param update map to fetch update from
     */
    public void updateSubscriberRangeCache(Long chatId, Map<String, BigDecimal> update) {
        var mapToUpdate = cachedRangeToNotNotifyAboutByChatId.get(chatId);
        if (mapToUpdate == null) {
            LOG.warn("For some reason subscriber to update doesn't have price ranges map.");
            return;
        }

        update.forEach((currencyName, price) ->
                mapToUpdate.put(currencyName, BigDecimalRange.prepareRange(price, properties.getPercentToNotify())));
    }

    /**
     * Remove bot subscriber.
     *
     * @param chatId id of the subscriber's chat
     */
    public void removeBotSubscriber(Long chatId) {
        if (cachedRangeToNotNotifyAboutByChatId.remove(chatId) != null) {
            botSubscriberRepository.removeByChatId(chatId);
        }
    }

    /**
     * Get amount of current bot subscribers.
     *
     * @return amount of current bot subscribers
     */
    public long countBotSubscribers() {
        return cachedRangeToNotNotifyAboutByChatId.size();
    }

    /**
     * Checks if the subscriber already exists.
     *
     * @param chatId id of the subscriber's chat
     * @return {@code true} if subscriber exists, {@code false} otherwise
     */
    public boolean exists(Long chatId) {
        return cachedRangeToNotNotifyAboutByChatId.containsKey(chatId);
    }

    /**
     * Gets map
     *
     * @return {@link Map} of subscriber chat id to his currency ranges to not notify about
     */
    public Map<Long, Map<String, BigDecimalRange>> getCachedRangeToNotNotifyAboutByChatId() {
        return cachedRangeToNotNotifyAboutByChatId;
    }

    /**
     * Generate range to not notify about by currency map.
     *
     * @param userInitialPrices the user initial prices
     * @return the map
     */
    private Map<String, BigDecimalRange> generateRangeToNotNotifyAboutByCurrency(Map<String, BigDecimal> userInitialPrices) {
        return userInitialPrices
                .entrySet()
                .stream()
                .collect(toConcurrentMap(
                        Map.Entry::getKey,
                        entry -> BigDecimalRange
                                .prepareRange(entry.getValue(), properties.getPercentToNotify())));
    }

    private void refreshCache() {
        cachedRangeToNotNotifyAboutByChatId.clear();
        botSubscriberRepository
                .findAll()
                .forEach(botSub -> {
                    var rangeToNotNotifyAboutByCurrency = generateRangeToNotNotifyAboutByCurrency(
                            botSub.getInitialCryptoPrices());

                    cachedRangeToNotNotifyAboutByChatId.put(botSub.getChatId(), rangeToNotNotifyAboutByCurrency);
                });
    }

    private void setupBotSubscriber(BotSubscriber botSubscriber) {
        cryptoPriceService.getCurrentCryptoPrices().forEach(price ->
                botSubscriber.getInitialCryptoPrices().put(price.getSymbol(), new BigDecimal(price.getPrice())));

        botSubscriberRepository.save(botSubscriber);

        cachedRangeToNotNotifyAboutByChatId.put(
                botSubscriber.getChatId(),
                generateRangeToNotNotifyAboutByCurrency(botSubscriber.getInitialCryptoPrices()));
    }
}