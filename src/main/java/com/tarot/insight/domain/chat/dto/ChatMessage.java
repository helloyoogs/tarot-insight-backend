package com.tarot.insight.domain.chat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    private String roomId;   // 채팅방 ID (예약 번호 활용 권장)
    private String sender;   // 보낸 사람 (닉네임)
    private String message;  // 내용
    private MessageType type; // ENTER(입장), TALK(채팅)

    public enum MessageType {
        ENTER, TALK
    }
}