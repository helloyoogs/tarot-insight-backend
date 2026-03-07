package com.tarot.insight.domain.tarot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TarotReadingRequest {
    private Long cardId;      // 뽑은 카드의 ID
    private String question;   // 유저의 질문
    private String resultText; // 해석 결과 (지금은 임시로 클라이언트가 보낸다고 가정)
}