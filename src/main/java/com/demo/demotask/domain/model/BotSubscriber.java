package com.demo.demotask.domain.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Bot Subscriber.
 *
 * @author Danylo Kovalchuk
 */
@Document("bot_subscriber")
public class BotSubscriber {

    @Id
    private ObjectId id;

    @Field("chat_id")
    private Long chatId;

    @Field("username")
    private String username;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("initial_crypto_prices")
    private Map<String, BigDecimal> initialCryptoPrices = new HashMap<>();

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, BigDecimal> getInitialCryptoPrices() {
        return initialCryptoPrices;
    }

    public void setInitialCryptoPrices(Map<String, BigDecimal> initialCryptoPrices) {
        this.initialCryptoPrices = initialCryptoPrices;
    }
}