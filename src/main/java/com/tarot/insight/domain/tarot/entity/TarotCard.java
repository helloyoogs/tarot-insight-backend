package com.tarot.insight.domain.tarot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "tarot_cards")
public class TarotCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_no")
    private Integer cardNo; // 0~77 등 카드 번호

    @Column(nullable = false, unique = true)
    private String name; // 예: The Fool, The Magician

    @Column(columnDefinition = "TEXT")
    private String description; // 카드의 기본 의미

    @Column(name = "image_url")
    private String imageUrl; // S3 등에 저장된 카드 이미지 경로
}