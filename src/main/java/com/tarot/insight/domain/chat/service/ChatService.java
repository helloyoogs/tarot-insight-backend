package com.tarot.insight.domain.chat.service;

import com.tarot.insight.domain.chat.dto.ChatMessage;
import com.tarot.insight.domain.chat.entity.ChatMessageEntity;
import com.tarot.insight.domain.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    @Transactional
    public void saveMessage(ChatMessage message) {
        ChatMessageEntity entity = ChatMessageEntity.builder()
                .roomId(message.getRoomId())
                .sender(message.getSender())
                .message(message.getMessage())
                .build();

        chatRepository.save(entity);
    }

    // 나중에 대화 내역 불러오기용
    public List<ChatMessageEntity> getChatHistory(String roomId) {
        return chatRepository.findAllByRoomIdOrderByCreatedAtAsc(roomId);
    }
}