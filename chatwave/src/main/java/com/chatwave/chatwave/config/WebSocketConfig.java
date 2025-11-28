package com.chatwave.chatwave.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {


        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Allow React to connect from localhost:3000
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Messages sent FROM server TO client start with "/topic"
        registry.enableSimpleBroker("/topic");

        // Messages sent FROM client TO server start with "/app"
        registry.setApplicationDestinationPrefixes("/app");
    }
}