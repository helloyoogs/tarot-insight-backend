package com.tarot.insight.domain.review.repository;

import com.tarot.insight.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 특정 상담사의 평균 평점 계산 (리뷰가 없을 경우 null 반환 방지를 위해 0.0 처리)
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.reservation.reader.id = :readerId")
    Double getAverageRatingByReaderId(@Param("readerId") Long readerId);
}
