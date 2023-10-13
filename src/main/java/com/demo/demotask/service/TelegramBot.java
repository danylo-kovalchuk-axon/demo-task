package com.demo.demotask.service;

import com.demo.demotask.config.DemoConfigProperties;
import com.demo.demotask.domain.model.BotSubscriber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Telegram Bot.
 *
 * @author Danylo Kovalchuk
 */
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(TelegramBot.class);
    private static final String WELCOME_MESSAGE = "Successfully subscribed!";
    private static final String FAREWELL_MESSAGE = "Successfully unsubscribed!";
    private static final String RESTART_SUCCESSFUL_MESSAGE = "Bot successfully restarted!";
    private static final String SUBSCRIBERS_OVERFLOW_MESSAGE = "Bot is unavailable";
    private static final String ALREADY_SUBSCRIBED_MESSAGE = "Already subscribed";
    private static final String SUBSCRIBER_NOT_EXISTS = "Subscriber not exists";
    private static final String AVAILABLE_COMMANDS_MESSAGE = """
            /start - subscribe to bot notifications about crypto price update
            /stop - to unsubscribe from notifications
            /restart - resubscribe to bot notifications
            """;
    private static final String START_COMMAND = "/start";
    private static final String STOP_COMMAND = "/stop";
    private static final String RESTART_COMMAND = "/restart";

    private final DemoConfigProperties config;

    @Autowired
    private BotSubscriberService botSubscriberService;

    @Autowired
    private TelegramBotsApi telegramBotsApi;

    /**
     * Instantiates a new Telegram bot.
     *
     * @param config the config
     */
    @Autowired
    public TelegramBot(DemoConfigProperties config) {
        super(config.getBot().getToken());
        this.config = config;
    }

    @PostConstruct
    private void registerBot() {
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            LOG.error("Bot registration failed due to: ", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        handleBotInput(update);
    }

    @Override
    public String getBotUsername() {
        return config.getBot().getUsername();
    }

    /* Private methods */

    private BotSubscriber prepareBotSub(Update update) {
        var botSub = new BotSubscriber();
        botSub.setChatId(update.getMessage().getChatId());
        botSub.setUsername(update.getMessage().getChat().getUserName());
        return botSub;
    }

    private void handleBotInput(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var text = update.getMessage().getText();
            switch (text) {
                case START_COMMAND -> handleBotStart(update);
                case RESTART_COMMAND -> handleBotRestart(update);
                case STOP_COMMAND -> handleBotStop(update);
                default -> sendAvailableCommandsMessage(update.getMessage().getChatId());
            }
        }
    }

    private void handleBotStart(Update update) {
        var chatId = update.getMessage().getChatId();
        if (botSubscriberService.exists(chatId)) {
            sendSubscriberAlreadyExistsMessage(chatId);
            return;
        }

        if (botSubscriberService.countBotSubscribers() >= config.getBot().getMaxUsers()) {
            sendSubscribersOverflowMessage(chatId);
            return;
        }

        botSubscriberService.createBotSubscriber(prepareBotSub(update));
        sendWelcomeMessage(chatId);
    }

    private void handleBotRestart(Update update) {
        var chatId = update.getMessage().getChatId();
        if (!botSubscriberService.exists(chatId)) {
            sendSubscriberNotExistsMessage(chatId);
            return;
        }

        botSubscriberService.refreshSubscriberInitialPrices(chatId);
        sendRestartSuccessfulMessage(chatId);
    }

    private void handleBotStop(Update update) {
        var chatId = update.getMessage().getChatId();
        if (!botSubscriberService.exists(chatId)) {
            sendSubscriberNotExistsMessage(chatId);
            return;
        }
        botSubscriberService.removeBotSubscriber(chatId);
        sendFarewellMessage(chatId);
    }


    private void sendSubscribersOverflowMessage(Long chatId) {
        sendMessage(new SendMessage(chatId.toString(), SUBSCRIBERS_OVERFLOW_MESSAGE));
    }

    private void sendSubscriberAlreadyExistsMessage(Long chatId) {
        sendMessage(new SendMessage(chatId.toString(), ALREADY_SUBSCRIBED_MESSAGE));
    }

    private void sendSubscriberNotExistsMessage(Long chatId) {
        sendMessage(new SendMessage(chatId.toString(), SUBSCRIBER_NOT_EXISTS));
    }

    private void sendAvailableCommandsMessage(Long chatId) {
        sendMessage(new SendMessage(chatId.toString(), AVAILABLE_COMMANDS_MESSAGE));
    }

    private void sendWelcomeMessage(Long chatId) {
        sendMessage(new SendMessage(chatId.toString(), WELCOME_MESSAGE));
    }

    private void sendFarewellMessage(Long chatId) {
        sendMessage(new SendMessage(chatId.toString(), FAREWELL_MESSAGE));
    }

    private void sendRestartSuccessfulMessage(Long chatId) {
        sendMessage(new SendMessage(chatId.toString(), RESTART_SUCCESSFUL_MESSAGE));
    }

    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Message send failed due to: " + e.getMessage());
        }
    }
}