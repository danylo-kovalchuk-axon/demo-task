package com.demo.demotask.domain.repository;

import com.demo.demotask.domain.model.BotSubscriber;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Bot Subscriber Repository.
 *
 * @author Danylo Kovalchuk
 */
public interface BotSubscriberRepository extends MongoRepository<BotSubscriber, ObjectId> {

    void removeByChatId(Long chatId);

    BotSubscriber getBotSubscriberByChatId(Long chatId);
}