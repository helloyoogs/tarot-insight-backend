package com.tarot.insight.domain.tarot.repository;

import com.tarot.insight.domain.tarot.entity.TarotInterpretation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarotInterpretationRepository extends JpaRepository<TarotInterpretation, Long> {

    List<TarotInterpretation> findByTheme_Id(Integer themeId);
}

