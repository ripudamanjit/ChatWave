package com.chatwave.chatwave.repository;

import com.chatwave.chatwave.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    // We add 'Pageable' as a parameter. Spring Data handles the rest.
    List<ChatMessage> findByChatId(String chatId, Pageable pageable);
}