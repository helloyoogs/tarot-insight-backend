package com.tarot.insight.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 웹소켓 메시지 핸들링 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. 클라이언트가 웹소켓에 접속할 엔드포인트 설정
        registry.addEndpoint("/ws-tarot")
                .setAllowedOriginPatterns("*").withSockJS(); // 프론트엔드 도메인 허용
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 2. 메시지를 보낼 때 (Publish) 사용하는 접두사
        registry.setApplicationDestinationPrefixes("/pub");

        // 3. 메시지를 받을 때 (Subscribe) 사용하는 접두사 (브로커)
        registry.enableSimpleBroker("/sub");
    }
}