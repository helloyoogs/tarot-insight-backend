package com.tarot.insight.domain.tarot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tarot_theme")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TarotTheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "theme_code", nullable = false, unique = true)
    private String themeCode; // LOVE, JOB_CHANGE, EXAM_PROMOTION, ...

    @Column(name = "theme_name", nullable = false)
    private String themeName; // 연애운, 취업/이직운 등
}

