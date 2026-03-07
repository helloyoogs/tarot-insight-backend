package com.tarot.insight.domain.tarot.dto;

import com.tarot.insight.domain.tarot.entity.TarotReading;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TarotReadingResponse {
    private Long id;
    private String cardName;    // 카드 이름 (예: The Fool)
    private String question;    // 질문 내용
    private String resultText;  // 리딩 결과
    private LocalDateTime createdAt; // 뽑은 날짜

    public TarotReadingResponse(TarotReading reading) {
        this.id = reading.getId();
        this.cardName = reading.getTarotCard().getName();
        this.question = reading.getQuestion();
        this.resultText = reading.getResultText();
        this.createdAt = reading.getCreatedAt();
    }
}