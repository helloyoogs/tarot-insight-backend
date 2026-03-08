package com.tarot.insight.domain.chat.entity;

import com.tarot.insight.domain.chat.dto.ChatMessage;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;   // 채팅방 ID
    private String sender;   // 보낸 사람

    @Column(columnDefinition = "TEXT")
    private String message;  // 대화 내용

    @Enumerated(EnumType.STRING)
    private ChatMessage.MessageType type;

    @CreatedDate
    private LocalDateTime createdAt; // 전송 시간

    @Builder
    public ChatMessageEntity(String roomId, String sender, String message, ChatMessage.MessageType type) {
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.type = type;
    }
}