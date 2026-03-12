package com.tarot.insight.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequest {
    @NotNull(message = "예약 ID는 필수입니다.")
    private Long reservationId;

    @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5 이하여야 합니다.")
    private int rating;

    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(max = 500, message = "리뷰 내용은 500자 이하여야 합니다.")
    private String comment;
}