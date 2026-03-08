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
        // 락 키를 생성할 때 고유성.
        String lockKey = "lock:reader:" + request.getReaderId() + ":" + request.getReservationTime();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 대기 시간을 10초로 넉넉히 늘리고, 락 유지 시간도 충분히.
            boolean available = lock.tryLock(10, 5, TimeUnit.SECONDS);

            if (!available) {
                throw new RuntimeException("이미 예약이 진행 중입니다.");
            }

            // 실제 서비스 호출 (여기서 예외가 발생해도 finally에서 락은 풀림.)
            return reservationService.createReservation(email, request);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }}