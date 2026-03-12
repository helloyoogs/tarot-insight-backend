package com.tarot.insight.domain.tarot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TarotReadingRequest {
    @NotNull(message = "카드 ID는 필수입니다.")
    private Long cardId;      // 뽑은 카드의 ID

    @NotBlank(message = "질문은 필수입니다.")
    @Size(max = 300, message = "질문은 300자 이하여야 합니다.")
    private String question;   // 유저의 질문

    @NotBlank(message = "리딩 결과 텍스트는 필수입니다.")
    @Size(max = 2000, message = "리딩 결과 텍스트는 2000자 이하여야 합니다.")
    private String resultText; // 해석 결과 (지금은 임시로 클라이언트가 보낸다고 가정)
}