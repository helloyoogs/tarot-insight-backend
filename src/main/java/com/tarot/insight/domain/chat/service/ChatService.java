package com.tarot.insight.domain.chat.service;

import com.tarot.insight.domain.chat.dto.ChatMessage;
import com.tarot.insight.domain.chat.entity.ChatMessageEntity;
import com.tarot.insight.domain.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로그용
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j // 로그 어노테이션
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    @Async
    @Transactional
    public void saveMessage(ChatMessage message) {
        // AsyncConfig에서 만든 "ChatAsync-X"가 찍혀야 성공.
        log.info(">>>> [비동기 작업 실행] 현재 쓰레드: {} | 메시지 저장 중...", Thread.currentThread().getName());

        ChatMessageEntity entity = ChatMessageEntity.builder()
                .roomId(message.getRoomId())
                .sender(message.getSender())
                .message(message.getMessage())
                .type(message.getType())
                .build();

        chatRepository.save(entity);
    }

    public List<ChatMessageEntity> getChatHistory(String roomId) {
        return chatRepository.findAllByRoomIdOrderByCreatedAtAsc(roomId);
    }
}