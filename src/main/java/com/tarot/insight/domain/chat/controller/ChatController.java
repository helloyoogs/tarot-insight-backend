package com.tarot.insight.domain.chat.controller;

import com.tarot.insight.domain.chat.dto.ChatMessage;
import com.tarot.insight.domain.chat.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final MessageListenerAdapter messageListenerAdapter;
    private final Map<String, ChannelTopic> topics = new HashMap<>();

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        String roomId = message.getRoomId();
        ChannelTopic topic = topics.get(roomId);

        // 1. 채팅방 토픽 관리
        if (topic == null) {
            topic = new ChannelTopic(roomId);
            redisMessageListenerContainer.addMessageListener(messageListenerAdapter, topic);
            topics.put(roomId, topic);
        }

        // 2. 입장 메시지 설정
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        // 3. Redis로 메시지 발행
        log.info(">>>> [컨트롤러] Redis로 메시지 발행: 방번호 {}", roomId);
        redisPublisher.publish(topic, message);
    }
}