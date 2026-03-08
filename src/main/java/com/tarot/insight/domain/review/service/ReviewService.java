package com.tarot.insight.domain.review.service;

import com.tarot.insight.domain.reservation.entity.ConsultationReservation;
import com.tarot.insight.domain.reservation.repository.ReservationRepository;
import com.tarot.insight.domain.review.dto.ReviewRequest;
import com.tarot.insight.domain.review.entity.Review;
import com.tarot.insight.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void createReview(ReviewRequest request) {
        // 1. 예약 정보 확인 및 상태 변경
        ConsultationReservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        reservation.complete(); // 상태를 COMPLETED로 변경

        // 2. 리뷰 저장
        Review review = Review.builder()
                .reservation(reservation)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        reviewRepository.save(review);

        // 3. ✨ 상담사 평점 실시간 업데이트
        Long readerId = reservation.getReader().getId();
        Double averageRating = reviewRepository.getAverageRatingByReaderId(readerId);

        reservation.getReader().updateRating(averageRating);
    }
}