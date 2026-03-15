package com.tarot.insight.domain.tarot.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoveTarotResponse {

    private final Integer themeId;
    private final String themeName;

    private final Long cardId;
    private final Integer cardNo;
    private final String cardName;
    private final String cardDescription;
    private final String cardImageUrl;
    private final String deckName;

    private final String situation;   // SOME / REUNION / COUPLE / SOLO / CRUSH
    private final String situationLabel; // 썸 / 재회 / 커플 / 솔로 / 짝사랑

    private final String resultText;  // 상황에 맞게 발췌된 한 문단
}

