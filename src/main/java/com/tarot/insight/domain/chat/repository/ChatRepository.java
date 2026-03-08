package com.tarot.insight.domain.chat.repository;

import com.tarot.insight.domain.chat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessageEntity, Long> {

    // 특정 채팅방(예약 번호)의 메시지 내역을 시간순으로 정렬해서 가져오기
    List<ChatMessageEntity> findAllByRoomIdOrderByCreatedAtAsc(String roomId);
}