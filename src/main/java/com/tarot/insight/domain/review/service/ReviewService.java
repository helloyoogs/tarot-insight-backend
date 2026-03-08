package com.tarot.insight.domain.review.service;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
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
    private final TarotReaderRepository readerRepository; // 추가 필요

    @Transactional
    public void createReview(ReviewRequest request) {
        // 1. 예약 정보 확인 및 상태 변경
        ConsultationReservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
        reservation.complete();

        // 2. 리뷰 저장
        Review review = Review.builder()
                .reservation(reservation)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        reviewRepository.save(review);

        // 3. ✨ 평점 업데이트 로직 호출
        updateReaderRating(reservation.getReader().getId());
    }

    // 테스트 코드에서 호출할 수 있도록 public으로 분리
    @Transactional
    public void updateReaderRating(Long readerId) {
        TarotReader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new IllegalArgumentException("상담사를 찾을 수 없습니다."));

        // DB에서 평균값 가져오기 (JPQL 혹은 Stream 사용)
        Double averageRating = reviewRepository.getAverageRatingByReaderId(readerId);

        if (averageRating == null) averageRating = 0.0;

        reader.updateRating(averageRating);
        readerRepository.save(reader); // 명시적 저장
    }
}