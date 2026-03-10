package com.tarot.insight.domain.reader.controller;

import com.tarot.insight.domain.reader.dto.ReaderSearchCondition;
import com.tarot.insight.domain.reader.dto.TarotReaderResponse;
import com.tarot.insight.domain.reader.service.TarotReaderRankingService; // ✨ 추가
import com.tarot.insight.domain.reader.service.TarotReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Tarot Reader API", description = "상담사 조회 및 실시간 랭킹 API")
@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class TarotReaderController {

    private final TarotReaderService tarotReaderService;
    private final TarotReaderRankingService rankingService;

    @GetMapping
    @Operation(summary = "전체 상담사 목록 조회", description = "활성화된 모든 상담사를 조회합니다. (Redis 캐시 적용)")
    public ResponseEntity<TarotReaderResponse[]> getAllReaders() {
        return ResponseEntity.ok(tarotReaderService.getAllReaders());
    }

    @GetMapping("/search")
    @Operation(summary = "상담사 동적 검색", description = "닉네임, 최소 경력, 최소 평점 등 조건에 맞는 상담사를 필터링합니다.")
    public ResponseEntity<List<TarotReaderResponse>> searchReaders(ReaderSearchCondition condition) {
        return ResponseEntity.ok(tarotReaderService.searchReaders(condition));
    }

    // Redis 기반 실시간 인기 순위 API
    @GetMapping("/ranking")
    @Operation(summary = "실시간 인기 상담사 TOP 5", description = "Redis ZSet을 활용하여 현재 예약이 가장 많은 상위 5명을 조회합니다.")
    public ResponseEntity<List<String>> getTopRanking() {
        // Redis에서 0~4위까지의 닉네임 리스트를 가져옵니다.
        return ResponseEntity.ok(rankingService.getTopRanking());
    }
}