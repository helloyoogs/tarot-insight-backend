package com.tarot.insight.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 추가
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) // [핵심 추가] 모르는 필드(@class 등)는 무시하라는 뜻
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String roomId;
    private String sender;
    private String message;
    private MessageType type;

    public enum MessageType {
        ENTER, TALK, QUIT
    }
}