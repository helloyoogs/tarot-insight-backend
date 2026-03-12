package com.tarot.insight.domain.reservation.entity;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.user.entity.User;
import com.tarot.insight.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ConsultationReservation extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 예약한 유저
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // 상담해줄 마스터
    @JoinColumn(name = "reader_id")
    private TarotReader reader;

    private LocalDateTime reservationTime; // 예약 시간

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // RESERVED 등

    @Version
    private Long version;

    public void complete() {
        this.status = ReservationStatus.COMPLETED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }
}