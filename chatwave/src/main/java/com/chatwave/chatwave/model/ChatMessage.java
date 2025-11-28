package com.chatwave.chatwave.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "messages")
public class ChatMessage {

    @Id
    private String id;
    private String chatId;    // The ID of the room or the person you are talking to
    private String senderId;  // The username or ID of the person sending the message
    private String content;   // The actual text
    private Date timestamp;

    public ChatMessage() {}

    public ChatMessage(String chatId, String senderId, String content) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = new Date();
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}