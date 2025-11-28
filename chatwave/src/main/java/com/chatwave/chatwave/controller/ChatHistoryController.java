package com.chatwave.chatwave.controller;

import com.chatwave.chatwave.model.ChatMessage;
import com.chatwave.chatwave.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class ChatHistoryController {

    @Autowired
    private ChatMessageService chatMessageService;

    @GetMapping("/{chatId}")
    public List<ChatMessage> getChatHistory(
            @PathVariable String chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        return chatMessageService.getChatHistory(chatId, page, size);
    }
}