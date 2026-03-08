package com.tarot.insight.domain.chat.repository;

import com.tarot.insight.domain.chat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessageEntity, Long> {

    // findAllBy... 라고 쓰면 해당 조건에 맞는 모든 데이터를 리스트로 가져옵니다.
    List<ChatMessageEntity> findAllByRoomIdOrderByCreatedAtAsc(String roomId);
}