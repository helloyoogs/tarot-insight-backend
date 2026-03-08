package com.tarot.insight.domain.reader.controller;

import com.tarot.insight.domain.reader.dto.TarotReaderResponse;
import com.tarot.insight.domain.reader.service.TarotReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class TarotReaderController {

    private final TarotReaderService tarotReaderService;

    @GetMapping
    public ResponseEntity<TarotReaderResponse[]> getAllReaders() {
        return ResponseEntity.ok(tarotReaderService.getAllReaders());
    }
}