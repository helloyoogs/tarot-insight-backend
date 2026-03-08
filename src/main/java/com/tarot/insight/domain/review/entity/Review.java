package com.tarot.insight.domain.review.entity;

import com.tarot.insight.domain.reservation.entity.ConsultationReservation;
import com.tarot.insight.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private ConsultationReservation reservation;

    private int rating; // 1~5점
    private String comment; // 후기 내용
}