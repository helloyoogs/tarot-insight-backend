package com.tarot.insight.domain.reader.controller;

import com.tarot.insight.domain.reader.dto.ReaderSearchCondition; // ✨ 추가
import com.tarot.insight.domain.reader.dto.TarotReaderResponse;
import com.tarot.insight.domain.reader.service.TarotReaderService;
import io.swagger.v3.oas.annotations.Operation; // ✨ Swagger용 추가
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List; // ✨ 추가

@Tag(name = "Tarot Reader API", description = "상담사 조회 및 검색 API")
@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class TarotReaderController {

    private final TarotReaderService tarotReaderService;

    @GetMapping
    @Operation(summary = "전체 상담사 목록 조회", description = "활성화된 모든 상담사를 조회합니다. (Redis 캐시 적용)")
    public ResponseEntity<TarotReaderResponse[]> getAllReaders() {
        return ResponseEntity.ok(tarotReaderService.getAllReaders());
    }

    // QueryDSL 기반 동적 검색 API
    @GetMapping("/search")
    @Operation(summary = "상담사 동적 검색", description = "닉네임, 최소 경력, 최소 평점 등 조건에 맞는 상담사를 필터링합니다.")
    public ResponseEntity<List<TarotReaderResponse>> searchReaders(ReaderSearchCondition condition) {
        // 쿼리 파라미터(?nickname=앨리스&minExperience=5)가 condition 객체로 자동 매핑.
        return ResponseEntity.ok(tarotReaderService.searchReaders(condition));
    }
}