package com.tarot.insight.domain.chat.entity;

import com.tarot.insight.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;
    private String sender;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Builder
    public ChatMessageEntity(String roomId, String sender, String message, MessageType type) {
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.type = type;
    }
}