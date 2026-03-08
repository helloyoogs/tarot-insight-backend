package com.tarot.insight.domain.review.controller;

import com.tarot.insight.domain.review.dto.ReviewRequest;
import com.tarot.insight.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> createReview(@RequestBody ReviewRequest request) {
        reviewService.createReview(request);
        return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다. 상담이 공식 종료되었습니다.");
    }
}