package com.tarot.insight.domain.review.service;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import com.tarot.insight.domain.reservation.entity.ConsultationReservation;
import com.tarot.insight.domain.reservation.repository.ReservationRepository;
import com.tarot.insight.domain.review.dto.ReviewRequest;
import com.tarot.insight.domain.review.entity.Review;
import com.tarot.insight.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final TarotReaderRepository readerRepository;

    /**
     * 리뷰 생성 (여기서 캐시 삭제를 관리하는 것이 가장 안전)
     */
    @Transactional
    @CacheEvict(value = "readers", allEntries = true) // 작업 전체가 성공하면 캐시 삭제!
    public void createReview(ReviewRequest request) {
        log.info(">>>> [리뷰 생성] 예약번호: {} | 캐시 무효화 예정", request.getReservationId());

        ConsultationReservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
        reservation.complete();

        Review review = Review.builder()
                .reservation(reservation)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        reviewRepository.save(review);

        // 평점 갱신 로직 실행 (내부 메서드 호출)
        this.updateReaderRating(reservation.getReader().getId());
    }

    /**
     * 상담사 평점 갱신 (내부 로직만 담당)
     */
    @Transactional // (내부 호출이라 이 어노테이션도 사실상 무시되지만, 개별 호출 대비 유지)
    public void updateReaderRating(Long readerId) {
        TarotReader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new IllegalArgumentException("상담사를 찾을 수 없습니다."));

        Double averageRating = reviewRepository.getAverageRatingByReaderId(readerId);
        if (averageRating == null) averageRating = 0.0;

        reader.updateRating(averageRating);
        log.info(">>>> [평점 갱신 완료] 상담사 ID: {}, 새로운 평점: {}", readerId, averageRating);
    }
}