package com.tarot.insight.domain.reservation.service;

import com.tarot.insight.domain.reservation.dto.ReservationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationFacade {

    private final RedissonClient redissonClient;
    private final ReservationService reservationService;

    /**
     * 분산 락을 적용한 예약 생성
     */
    public Long createReservation(String email, ReservationRequest request) {
        String lockKey = "lock:reader:" + request.getReaderId() + ":" + request.getReservationTime();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 이렇게 하면 Redisson의 'Watchdog' 기능이 활성화되어 작업이 끝날 때까지 락을 안전하게 지켜줍니다.
            boolean available = lock.tryLock(15, TimeUnit.SECONDS);

            if (!available) {
                throw new RuntimeException("이미 예약이 진행 중입니다.");
            }

            return reservationService.createReservation(email, request);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 반드시 현재 쓰레드가 락을 가지고 있을 때만 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}