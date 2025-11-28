package com.chatwave.chatwave.service;

import com.chatwave.chatwave.model.ChatMessage;
import com.chatwave.chatwave.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private FilterService filterService;

    // LOGIC 1: Save Message (Used by WebSocket)
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        // 1. Filter the content
        String cleanContent = filterService.filterMessage(chatMessage.getContent());
        chatMessage.setContent(cleanContent);

        // 2. Save to DB
        return chatMessageRepository.save(chatMessage);
    }

    // LOGIC 2: Get History
    public List<ChatMessage> getChatHistory(String chatId, int page, int size) {
        // 1. Create Pagination (Newest First)
        Pageable paging = PageRequest.of(page, size, Sort.by("timestamp").descending());

        // 2. Fetch from DB
        return chatMessageRepository.findByChatId(chatId, paging);
    }
}