package com.tarot.insight.domain.reader.repository;

import com.tarot.insight.domain.reader.entity.TarotReader;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TarotReaderRepository extends JpaRepository<TarotReader, Long> {
    // 상담 가능 상태인 상담사들만 조회
    List<TarotReader> findAllByIsActiveTrue();
}