package com.tarot.insight.domain.tarot.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ThemeTarotResponse {

    private final Integer themeId;
    private final String themeName;

    private final Integer cardNo;
    private final String cardName;
    private final String cardDescription;
    private final String cardImageUrl;
    private final String deckName;

    private final String resultText;
}

