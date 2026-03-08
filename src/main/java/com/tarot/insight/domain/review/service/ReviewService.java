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
        // 1. 해당 예약 찾기
        ConsultationReservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        // 2. 상담 완료 처리 (상태 변경)
        // Reservation 엔티티에 @Setter가 없다면 상태 변경 메서드를 추가하는 것이 좋습니다.
        reservation.complete();

        // 3. 리뷰 저장
        Review review = Review.builder()
                .reservation(reservation)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        reviewRepository.save(review);
    }
}