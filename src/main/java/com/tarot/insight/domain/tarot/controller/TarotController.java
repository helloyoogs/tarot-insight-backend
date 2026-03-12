package com.tarot.insight.domain.tarot.controller;

import com.tarot.insight.domain.tarot.dto.TarotReadingRequest;
import com.tarot.insight.domain.tarot.dto.TarotReadingResponse;
import com.tarot.insight.domain.tarot.service.TarotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarot")
@RequiredArgsConstructor
public class TarotController {

    private final TarotService tarotService;

    @PostMapping("/reading")
    public ResponseEntity<String> saveReading(
            Authentication authentication, // JWT 필터가 넣어준 인증 정보
            @Valid @RequestBody TarotReadingRequest request) {

        String email = authentication.getName(); // 토큰에서 이메일 추출
        Long readingId = tarotService.saveReading(email, request);

        return ResponseEntity.ok("타로 리딩 기록이 저장되었습니다. ID: " + readingId);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TarotReadingResponse>> getMyHistory(Authentication authentication) {
        String email = authentication.getName();
        List<TarotReadingResponse> history = tarotService.getMyHistory(email);
        return ResponseEntity.ok(history);
    }
}