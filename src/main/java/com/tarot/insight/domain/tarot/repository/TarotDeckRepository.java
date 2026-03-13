package com.tarot.insight.domain.tarot.repository;

import com.tarot.insight.domain.tarot.entity.TarotDeck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TarotDeckRepository extends JpaRepository<TarotDeck, Integer> {

    Optional<TarotDeck> findByDeckCode(String deckCode);
}

