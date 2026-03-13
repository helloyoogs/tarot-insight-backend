package com.tarot.insight.domain.tarot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoveTarotRequest {

    /**
     * 연애 상황 코드
     * - SOME     : 썸
     * - REUNION  : 재회
     * - COUPLE   : 커플
     * - SOLO     : 솔로
     * - CRUSH    : 짝사랑
     */
    @NotBlank(message = "연애 상황 코드는 필수입니다.")
    private String situation;
}

