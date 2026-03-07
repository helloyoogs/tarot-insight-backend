package com.tarot.insight.domain.tarot.repository;

import com.tarot.insight.domain.tarot.entity.TarotReading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TarotReadingRepository extends JpaRepository<TarotReading, Long> {
    // 특정 사용자의 상담 기록만 모아보기 위한 메서드
    List<TarotReading> findAllByUserId(Long userId);
}