package Student_ride_sharing.demand.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Tells Spring this app uses WebSockets
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // "/topic" is the prefix for outgoing broadcast channels (Spring -> React)
        config.enableSimpleBroker("/topic");
        // "/app" is the prefix for incoming messages (React -> Spring)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // "/ws-tracking" is the actual URL the React frontend will connect to.
        // SockJS acts as a fallback wrapper if standard WebSockets fail.
        registry.addEndpoint("/ws-tracking")
                .setAllowedOriginPatterns("*") // Allows cross-origin requests from React (localhost:5173)
                .withSockJS();
    }
}