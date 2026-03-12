package com.tarot.insight.domain.user.dto;

import com.tarot.insight.domain.user.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Schema(example = "testuser@tarot.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(example = "password123!")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Schema(example = "테스트고객")
    private String nickname;

    // 권한 필드!
    @Schema(example = "USER", description = "권한 (USER 또는 READER)")
    private UserRole role;
}