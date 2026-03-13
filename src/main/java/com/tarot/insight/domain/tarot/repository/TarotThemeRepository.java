package com.tarot.insight.domain.tarot.repository;

import com.tarot.insight.domain.tarot.entity.TarotTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TarotThemeRepository extends JpaRepository<TarotTheme, Integer> {

    Optional<TarotTheme> findByThemeCode(String themeCode);
}

