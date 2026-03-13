package com.tarot.insight.domain.tarot.controller;

import com.tarot.insight.domain.tarot.dto.LoveTarotRequest;
import com.tarot.insight.domain.tarot.dto.LoveTarotResponse;
import com.tarot.insight.domain.tarot.dto.ThemeTarotRequest;
import com.tarot.insight.domain.tarot.dto.ThemeTarotResponse;
import com.tarot.insight.domain.tarot.service.TarotThemeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tarot/themes")
@RequiredArgsConstructor
public class TarotThemeController {

    private final TarotThemeService tarotThemeService;

    /**
     * [연애운] 객관식 질문 기반 타로
     * - 상황 코드(SOME/REUNION/COUPLE/SOLO/CRUSH)에 따라
     *   동일 카드의 result_text 안에서 해당 구간만 발췌해서 반환한다.
     */
    @PostMapping("/love")
    public ResponseEntity<LoveTarotResponse> drawLoveTarot(@Valid @RequestBody LoveTarotRequest request) {
        LoveTarotResponse response = tarotThemeService.drawLoveTarot(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 그 외 테마별 월간 운세
     * - themeId: 2~7
     * - 단순히 해당 테마에서 랜덤 카드 1장을 뽑아 result_text 전체를 반환한다.
     */
    @PostMapping("/monthly")
    public ResponseEntity<ThemeTarotResponse> drawThemeTarot(@Valid @RequestBody ThemeTarotRequest request) {
        ThemeTarotResponse response = tarotThemeService.drawThemeTarot(request);
        return ResponseEntity.ok(response);
    }

    /**
     * [오늘의 운세]
     * - themeId = 8 로 고정되어 있다고 가정.
     */
    @GetMapping("/daily")
    public ResponseEntity<ThemeTarotResponse> drawDailyTarot() {
        ThemeTarotResponse response = tarotThemeService.drawDailyTarot();
        return ResponseEntity.ok(response);
    }
}

