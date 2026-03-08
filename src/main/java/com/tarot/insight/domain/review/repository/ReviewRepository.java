package com.tarot.insight.domain.review.repository;

import com.tarot.insight.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 1. [테스트용] 특정 상담사의 모든 리뷰 목록 조회
    // 테스트 코드에서 given(...) 안에 사용하기 위해 필요합니다.
    @Query("SELECT r FROM Review r WHERE r.reservation.reader.id = :readerId")
    List<Review> findAllByReaderId(@Param("readerId") Long readerId);

    // 2. [실제 서비스용] 특정 상담사의 평균 평점 계산
    // COALESCE를 사용해 리뷰가 없을 경우 0.0을 반환하도록 설계한 아주 좋은 코드입니다!
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.reservation.reader.id = :readerId")
    Double getAverageRatingByReaderId(@Param("readerId") Long readerId);
}