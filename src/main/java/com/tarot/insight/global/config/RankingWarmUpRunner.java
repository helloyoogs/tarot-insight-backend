package com.tarot.insight.global.config;

import com.tarot.insight.domain.reader.service.TarotReaderRankingService;
import com.tarot.insight.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class RankingWarmUpRunner implements ApplicationRunner {

    private final ReservationRepository reservationRepository;
    private final TarotReaderRankingService rankingService;

    @Override
    @Transactional(readOnly = true) // 세션을 메서드 끝날 때까지 유지합니다!
    public void run(ApplicationArguments args) {
        log.info("🚀 [Warm-up] Redis 랭킹 데이터 복구 시작...");

        reservationRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        res -> res.getReader().getUser().getNickname(),
                        Collectors.counting()
                ))
                .forEach((nickname, count) -> {
                    for (int i = 0; i < count; i++) {
                        rankingService.incrementScore(nickname);
                    }
                });

        log.info("✅ [Warm-up] Redis 랭킹 데이터 복구 완료!");
    }
}