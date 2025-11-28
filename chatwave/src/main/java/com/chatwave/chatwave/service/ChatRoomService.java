package com.chatwave.chatwave.service;

import com.chatwave.chatwave.model.ChatRoom;
import com.chatwave.chatwave.repository.ChatRoomRepository;
import com.chatwave.chatwave.repository.UserRepository; // Import UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    // Method 1: For Private Chats
    public ChatRoom createPrivateChat(ChatRoom chatRoom) {
        chatRoom.setType("PRIVATE");

        ChatRoom existingChat = chatRoomRepository.findExistingPrivateChat(chatRoom.getParticipants());
        if (existingChat != null) {
            return existingChat;
        }

        return chatRoomRepository.save(chatRoom);
    }

    // Method 2: For Group Chats
    public ChatRoom createGroup(ChatRoom chatRoom) {
        chatRoom.setType("GROUP");

        // 1. Validate Group Name
        if (chatRoom.getName() == null || chatRoom.getName().trim().isEmpty()) {
            throw new RuntimeException("Group chats must have a name!");
        }

        List<String> participants = chatRoom.getParticipants();

        // 2. Validate Minimum Participants (Must be at least 3)
        if (participants == null || participants.size() < 3) {
            throw new RuntimeException("A group must have at least 3 participants.");
        }

        // 3. Validate that ALL users exist
        for (String username : participants) {
            // We use the existsByUsername method you defined in UserRepository
            if (!userRepository.existsByUsername(username)) {
                throw new RuntimeException("User '" + username + "' does not exist.");
            }
        }


        return chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoom> getChatsForUser(String username) {
        return chatRoomRepository.findByParticipantsContaining(username);
    }



    public ChatRoom addParticipantToGroup(String chatId, String newUsername) {
        // 1. Check if the user we want to add actually exists
        if (!userRepository.existsByUsername(newUsername)) {
            throw new RuntimeException("User '" + newUsername + "' does not exist.");
        }

        // 2. Find the chat group
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        // 3. Ensure it is a GROUP
        if (!"GROUP".equalsIgnoreCase(chatRoom.getType())) {
            throw new RuntimeException("Cannot add participants to a private chat.");
        }

        // 4. Check if user is already in the group
        if (chatRoom.getParticipants().contains(newUsername)) {
            throw new RuntimeException("User is already in this group.");
        }

        // 5. Add user and save
        chatRoom.getParticipants().add(newUsername);
        return chatRoomRepository.save(chatRoom);
    }
}