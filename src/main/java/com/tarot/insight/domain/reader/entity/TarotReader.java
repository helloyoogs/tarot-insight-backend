package com.tarot.insight.domain.reader.entity;

import com.tarot.insight.domain.user.User;
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

    // 1:1 관계 - 한 명의 유저는 하나의 상담사 프로필만 가질 수 있습니다.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String profile; // 상담사 자기소개

    private int experienceYears; // 경력 (년수)

    @Builder.Default
    private Double rating = 0.0; // 평균 평점

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true; // 현재 상담 가능 여부

    public void updateRating(Double newRating) {
        this.rating = newRating;
    }
}