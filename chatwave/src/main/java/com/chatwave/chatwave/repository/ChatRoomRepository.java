package com.chatwave.chatwave.repository;

import com.chatwave.chatwave.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

    // 1. Find all chats a specific user is part of
    List<ChatRoom> findByParticipantsContaining(String username);

    // 2. Custom Query to check if a private chat already exists
    // This checks if the participants list contains ALL the names provided (?0)
    // AND the type is 'PRIVATE'
    @Query("{ 'participants': { $all: ?0 }, 'type': 'PRIVATE' }")
    ChatRoom findExistingPrivateChat(List<String> participants);
}