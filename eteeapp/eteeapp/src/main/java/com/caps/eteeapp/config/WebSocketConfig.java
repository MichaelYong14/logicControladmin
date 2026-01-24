package com.caps.eteeapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker with prefixes
        config.enableSimpleBroker("/topic", "/queue");
        // Set application destination prefix for messages from clients
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the WebSocket endpoint with SockJS fallback
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("http://localhost:3000", "https://eteeap.netlify.app")
            .setAllowedOrigins("http://localhost:3000", "https://eteeap.netlify.app")
            .withSockJS();
    }
}