package com.tarot.insight.domain.review.service;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import com.tarot.insight.domain.reservation.entity.ConsultationReservation;
import com.tarot.insight.domain.reservation.repository.ReservationRepository;
import com.tarot.insight.domain.review.dto.ReviewRequest;
import com.tarot.insight.domain.review.entity.Review;
import com.tarot.insight.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // [추가]
import org.springframework.cache.annotation.CacheEvict; // [추가]
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j // [추가]
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final TarotReaderRepository readerRepository;

    @Transactional
    public void createReview(ReviewRequest request) {
        ConsultationReservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
        reservation.complete();

        Review review = Review.builder()
                .reservation(reservation)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        reviewRepository.save(review);

        updateReaderRating(reservation.getReader().getId());
    }

    /**
     * 상담사 평점 갱신 및 관련 캐시 삭제
     * value: 삭제할 캐시 이름 (TarotReaderService에서 설정한 이름과 같아야 함)
     * allEntries = true: 'readers' 캐시 안에 있는 모든 데이터(전체 목록 등)를 싹 지움
     */
    @Transactional
    @CacheEvict(value = "readers", allEntries = true) // 평점이 바뀌면 캐시를 비웁니다.
    public void updateReaderRating(Long readerId) {
        log.info(">>>> [캐시 삭제] 상담사(ID: {})의 평점이 갱신되어 'readers' 캐시를 초기화합니다.", readerId);

        TarotReader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new IllegalArgumentException("상담사를 찾을 수 없습니다."));

        Double averageRating = reviewRepository.getAverageRatingByReaderId(readerId);
        if (averageRating == null) averageRating = 0.0;

        reader.updateRating(averageRating);
        readerRepository.save(reader);
    }
}