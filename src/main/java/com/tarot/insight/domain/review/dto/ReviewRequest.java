package com.tarot.insight.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequest {
    private Long reservationId;
    private int rating;
    private String comment;
}