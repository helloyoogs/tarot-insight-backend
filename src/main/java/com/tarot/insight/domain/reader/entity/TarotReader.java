package com.tarot.insight.domain.reader.entity;

import com.tarot.insight.domain.user.entity.User;
import com.tarot.insight.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "tarot_readers")
public class TarotReader extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String profile;

    private int experienceYears;

    @Builder.Default
    private Double rating = 0.0;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    // 기존 평점 업데이트 메서드
    public void updateRating(Double rating) {
        this.rating = rating;
    }

    /**
     * 프로필 정보 수정 비즈니스 로직
     * 외부에서 필드를 직접 수정(Setter)하지 못하게 하고,
     * 의미 있는 메서드를 통해 데이터를 변경합니다.
     */
    public void updateProfile(String profile, int experienceYears) {
        this.profile = profile;
        this.experienceYears = experienceYears;
    }
}