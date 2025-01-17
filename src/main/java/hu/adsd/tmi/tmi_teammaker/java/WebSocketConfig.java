package hu.adsd.tmi.tmi_teammaker.java;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Configure the message broker
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Enable a simple message broker for the "/topic" destination prefix
        config.setApplicationDestinationPrefixes("/app"); // Set the application destination prefix to "/app"
    }

    // Register the STOMP endpoints
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket") // Register the "/websocket" endpoint
                .setAllowedOriginPatterns("http://localhost:*") // Allow requests from "http://localhost" with any port
                .withSockJS(); // Enable SockJS support for fallback options
    }
}

