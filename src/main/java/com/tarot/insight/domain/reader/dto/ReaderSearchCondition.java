package com.tarot.insight.domain.reader.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReaderSearchCondition {
    private String nickname;      // 상담사 닉네임 검색
    private Integer minExperience; // 최소 경력 (n년 이상)
    private Double minRating;     // 최소 평점 (n점 이상)
}