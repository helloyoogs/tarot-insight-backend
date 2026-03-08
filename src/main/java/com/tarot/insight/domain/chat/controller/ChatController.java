package com.tarot.insight.domain.chat.controller;

import com.tarot.insight.domain.chat.dto.ChatMessage;
import com.tarot.insight.domain.chat.service.ChatService;
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
    private final ChatService chatService;

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
            // [참고] 입장(ENTER) 신호는 저장하지 않고 바로 전송만 합니다.
        } else if (ChatMessage.MessageType.TALK.equals(message.getType())) {
            // 3. [핵심 수정] 실제 대화(TALK)일 때만 DB에 영구 저장합니다.
            chatService.saveMessage(message);
            log.info(">>>> [DB 저장 완료] 방번호: {}, 타입: {}", roomId, message.getType());
        }

        // 4. Redis로 메시지 발행 (ENTER든 TALK든 실시간으로 화면에는 보여줘야 하니까요!)
        redisPublisher.publish(topic, message);
    }
}