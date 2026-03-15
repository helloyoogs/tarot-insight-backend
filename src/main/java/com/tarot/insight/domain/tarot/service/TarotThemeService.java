package com.tarot.insight.domain.tarot.service;

import com.tarot.insight.domain.tarot.dto.LoveTarotRequest;
import com.tarot.insight.domain.tarot.dto.LoveTarotResponse;
import com.tarot.insight.domain.tarot.dto.ThemeTarotRequest;
import com.tarot.insight.domain.tarot.dto.ThemeTarotResponse;
import com.tarot.insight.domain.tarot.entity.TarotInterpretation;
import com.tarot.insight.domain.tarot.repository.TarotInterpretationRepository;
import com.tarot.insight.global.error.ErrorCode;
import com.tarot.insight.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TarotThemeService {

    private static final int LOVE_THEME_ID = 1;
    private static final int DAILY_THEME_ID = 8;

    private final TarotInterpretationRepository tarotInterpretationRepository;

    public LoveTarotResponse drawLoveTarot(LoveTarotRequest request) {
        LoveSituation situation = LoveSituation.fromCode(request.getSituation());

        TarotInterpretation interpretation = drawRandomByTheme(LOVE_THEME_ID);

        String extracted = extractBySituation(interpretation.getResultText(), situation);
        var card = interpretation.getTarotCard();

        return LoveTarotResponse.builder()
                .themeId(interpretation.getTheme().getId())
                .themeName(interpretation.getTheme().getThemeName())
                .cardId(card.getId())
                .cardNo(card.getCardNo())
                .cardName(card.getName())
                .cardDescription(card.getDescription())
                .cardImageUrl(card.getImageUrl())
                .deckName(interpretation.getDeck().getDeckName())
                .situation(situation.name())
                .situationLabel(situation.getLabel())
                .resultText(extracted)
                .build();
    }

    public ThemeTarotResponse drawThemeTarot(ThemeTarotRequest request) {
        TarotInterpretation interpretation = drawRandomByTheme(request.getThemeId());
        var card = interpretation.getTarotCard();

        return ThemeTarotResponse.builder()
                .themeId(interpretation.getTheme().getId())
                .themeName(interpretation.getTheme().getThemeName())
                .cardId(card.getId())
                .cardNo(card.getCardNo())
                .cardName(card.getName())
                .cardDescription(card.getDescription())
                .cardImageUrl(card.getImageUrl())
                .deckName(interpretation.getDeck().getDeckName())
                .resultText(interpretation.getResultText())
                .build();
    }

    public ThemeTarotResponse drawDailyTarot() {
        TarotInterpretation interpretation = drawRandomByTheme(DAILY_THEME_ID);
        var card = interpretation.getTarotCard();

        return ThemeTarotResponse.builder()
                .themeId(interpretation.getTheme().getId())
                .themeName(interpretation.getTheme().getThemeName())
                .cardId(card.getId())
                .cardNo(card.getCardNo())
                .cardName(card.getName())
                .cardDescription(card.getDescription())
                .cardImageUrl(card.getImageUrl())
                .deckName(interpretation.getDeck().getDeckName())
                .resultText(interpretation.getResultText())
                .build();
    }

    private TarotInterpretation drawRandomByTheme(Integer themeId) {
        List<TarotInterpretation> list = tarotInterpretationRepository.findByTheme_Id(themeId);
        if (list.isEmpty()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        int index = ThreadLocalRandom.current().nextInt(list.size());
        return list.get(index);
    }

    /**
     * result_text 내부에 [썸] ... [재회] ... 같은 구조로 문단이 들어있다는 전제 하에
     * 상황에 맞는 구간만 잘라내서 반환.
     * 해당 태그가 없으면 전체 텍스트를 그대로 반환한다.
     */
    private String extractBySituation(String fullText, LoveSituation situation) {
        if (fullText == null || fullText.isBlank()) {
            return "";
        }

        String tag = "[" + situation.getLabel() + "]";
        int start = fullText.indexOf(tag);
        if (start < 0) {
            return fullText.trim();
        }

        // 태그 바로 뒤부터 내용 시작
        start = start + tag.length();

        // 다음 태그(예: [재회], [커플] 등)가 나오기 전까지를 잘라낸다.
        int nextTag = fullText.indexOf("[", start);
        String slice = (nextTag > start) ? fullText.substring(start, nextTag) : fullText.substring(start);

        return slice.trim();
    }

    public enum LoveSituation {
        SOME("SOME", "썸"),
        REUNION("REUNION", "재회"),
        COUPLE("COUPLE", "커플"),
        SOLO("SOLO", "솔로"),
        CRUSH("CRUSH", "짝사랑");

        private final String code;
        private final String label;

        LoveSituation(String code, String label) {
            this.code = code;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static LoveSituation fromCode(String code) {
            if (code == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            String upper = code.toUpperCase(Locale.ROOT).trim();
            for (LoveSituation value : values()) {
                if (value.code.equals(upper)) {
                    return value;
                }
            }
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}

