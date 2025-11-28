package com.chatwave.chatwave.controller;

import com.chatwave.chatwave.model.ChatRoom;
import com.chatwave.chatwave.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    // 1.  Create Private Chat (1-on-1)

    @PostMapping("/create")
    public ResponseEntity<?> createPrivateChat(@RequestBody ChatRoom chatRoom) {
        try {
            ChatRoom createdChat = chatRoomService.createPrivateChat(chatRoom);
            return ResponseEntity.ok(createdChat);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 2.  Create Group Chat

    @PostMapping("/create-group")
    public ResponseEntity<?> createGroup(@RequestBody ChatRoom chatRoom) {
        try {
            ChatRoom createdGroup = chatRoomService.createGroup(chatRoom);
            return ResponseEntity.ok(createdGroup);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Get all chats for a user
    @GetMapping("/user/{username}")
    public List<ChatRoom> getUserChats(@PathVariable String username) {
        return chatRoomService.getChatsForUser(username);
    }

    //

    @PutMapping("/{chatId}/add")
    public ResponseEntity<?> addParticipant(@PathVariable String chatId, @RequestBody String newUsername) {
        try {

            String cleanUsername = newUsername.replace("\"", "").trim();

            ChatRoom updatedChat = chatRoomService.addParticipantToGroup(chatId, cleanUsername);
            return ResponseEntity.ok(updatedChat);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}