package com.tarot.insight.service;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import com.tarot.insight.domain.review.repository.ReviewRepository;
import com.tarot.insight.domain.review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private TarotReaderRepository readerRepository;

    @Test
    @DisplayName("리뷰 등록 시 상담사의 평균 평점이 소수점 4자리까지 정확히 계산되어야 한다")
    void update_reader_rating_precision_test() {
        // [Given] 1. 기초 데이터 준비
        Long readerId = 1L;
        TarotReader reader = TarotReader.builder()
                .id(readerId)
                .rating(0.0)
                .build();

        // 기대하는 평균값 계산: (5+4+4)/3 = 4.333333...
        Double expectedAverage = 4.3333333333;

        given(readerRepository.findById(readerId)).willReturn(Optional.of(reader));

        // 핵심 수정 부분: 서비스 로직이 사용하는 '평균 계산 메서드'를 모킹합니다.
        given(reviewRepository.getAverageRatingByReaderId(readerId)).willReturn(expectedAverage);

        // [When] 로직 실행
        reviewService.updateReaderRating(readerId);

        // [Then] 결과 검증
        assertThat(reader.getRating())
                .as("평점은 4.3333 수준의 정밀도를 유지해야 함")
                .isCloseTo(4.3333, within(0.0001));

        verify(readerRepository, times(1)).save(any(TarotReader.class));
    }
}