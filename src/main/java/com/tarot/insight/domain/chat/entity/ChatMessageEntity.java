package com.tarot.insight.domain.chat.entity;

import com.tarot.insight.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "chat_messages")
public class ChatMessageEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;   // 예약 번호(Reservation ID)를 방 번호로 사용
    private String sender;   // 보낸 사람 닉네임

    @Column(columnDefinition = "TEXT")
    private String message;  // 채팅 내용
}