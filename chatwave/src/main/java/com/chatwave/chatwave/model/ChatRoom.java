package com.chatwave.chatwave.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "chats")
public class ChatRoom {

    @Id
    private String id;              // This is the 'chatId' you will use in messages
    private String name;            // Name of the group (e.g., "Weekend Plans")
    private String type;            // "PRIVATE" or "GROUP"
    private List<String> participants; // List of Usernames or User IDs


    public ChatRoom() {}

    public ChatRoom(String name, String type, List<String> participants) {
        this.name = name;
        this.type = type;
        this.participants = participants;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }
}