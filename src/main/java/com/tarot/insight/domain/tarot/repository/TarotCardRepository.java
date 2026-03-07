package com.tarot.insight.domain.tarot.repository;

import com.tarot.insight.domain.tarot.entity.TarotCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarotCardRepository extends JpaRepository<TarotCard, Long> {
}