package com.tarot.insight.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarot.insight.domain.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 1. Redis에서 온 생(Raw) 데이터를 로그로 찍어봅니다.
            String publishMessage = new String(message.getBody());
            log.info(">>>> [Redis 수신신호] raw 데이터: {}", publishMessage);

            // 2. 객체로 변환
            ChatMessage chatMessage = objectMapper.readValue(message.getBody(), ChatMessage.class);
            log.info(">>>> [변환 성공] 보낸이: {}, 방번호: {}", chatMessage.getSender(), chatMessage.getRoomId());

            // 3. 웹소켓으로 쏘기 (목적지 로그 확인 필수!)
            String destination = "/sub/chat/room/" + chatMessage.getRoomId();
            log.info(">>>> [최종 발송] 목적지: {}", destination);

            messagingTemplate.convertAndSend(destination, chatMessage);

        } catch (Exception e) {
            // 에러가 나면 무조건 여기에 찍힙니다.
            log.error("!!!! [에러 발생] Redis 구독 처리 중 문제 발생: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}