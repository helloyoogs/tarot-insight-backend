package com.tarot.insight.domain.tarot.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ThemeTarotRequest {

    /**
     * 테마 ID
     * 2: 취업/이직운
     * 3: 합격/승진운
     * 4: 금전/사업운
     * 5~7: 기타 월간 테마
     */
    @NotNull(message = "테마 ID는 필수입니다.")
    @Min(value = 2, message = "테마 ID는 2 이상이어야 합니다.")
    @Max(value = 7, message = "테마 ID는 7 이하여야 합니다.")
    private Integer themeId;
}

