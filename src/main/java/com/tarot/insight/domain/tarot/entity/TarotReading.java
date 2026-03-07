package com.tarot.insight.domain.tarot.entity;

import com.tarot.insight.domain.user.User;
import com.tarot.insight.global.entity.BaseTimeEntity; // 생성시간 자동 저장을 위해 필요 (아래 설명 참고)
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "tarot_readings")
public class TarotReading extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N:1 관계 - 한 명의 유저는 여러 번 타로를 볼 수 있습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // N:1 관계 - 하나의 카드는 여러 리딩 기록에 등장할 수 있습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarot_card_id")
    private TarotCard tarotCard;

    @Column(nullable = false)
    private String question; // 사용자가 입력한 질문

    @Column(columnDefinition = "TEXT")
    private String resultText; // 해석 결과 (나중에 AI가 생성해 줄 내용)
}