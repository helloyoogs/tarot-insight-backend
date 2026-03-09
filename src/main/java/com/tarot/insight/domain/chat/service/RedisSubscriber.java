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
    private final ChatService chatService; // [추가] 비동기 저장을 위한 서비스 주입

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 1. Redis에서 온 생(Raw) 데이터 로그
            String publishMessage = new String(message.getBody());
            log.info(">>>> [Redis 수신신호] raw 데이터: {}", publishMessage);

            // 2. 객체로 변환
            ChatMessage chatMessage = objectMapper.readValue(message.getBody(), ChatMessage.class);
            log.info(">>>> [변환 성공] 보낸이: {}, 방번호: {}", chatMessage.getSender(), chatMessage.getRoomId());

            // 3. 웹소켓으로 쏘기 (전송 속도 최우선!)
            String destination = "/sub/chat/room/" + chatMessage.getRoomId();
            messagingTemplate.convertAndSend(destination, chatMessage);
            log.info(">>>> [최종 발송] 목적지: {}", destination);

            // 4. ✨ 드디어 비동기 DB 저장 호출!
            // ChatService의 @Async 덕분에 DB 저장이 완료될 때까지 기다리지 않고
            // 이 onMessage 메서드는 즉시 종료됩니다.
            chatService.saveMessage(chatMessage);
            log.info(">>>> [비동기 저장] ChatAsync 쓰레드에게 DB 저장 위임 완료");

        } catch (Exception e) {
            log.error("!!!! [에러 발생] Redis 구독 처리 중 문제 발생: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}