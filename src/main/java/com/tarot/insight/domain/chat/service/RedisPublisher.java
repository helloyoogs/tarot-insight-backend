package com.tarot.insight.domain.chat.service;

import com.tarot.insight.domain.chat.dto.ChatMessage; // 기존 객체 사용
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, ChatMessage message) {
        // ChatMessage 객체를 Redis 토픽으로 발행합니다.
        // 이때 객체는 자동으로 JSON 문자열로 직렬화되어 전송됩니다.
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}