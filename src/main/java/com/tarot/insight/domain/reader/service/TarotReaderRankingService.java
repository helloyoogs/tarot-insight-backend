package com.tarot.insight.domain.reader.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate; // ✨ 추가
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TarotReaderRankingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate; // ✨ 실시간 알림 도구 주입
    private static final String RANKING_KEY = "reader:ranking";

    /**
     * 1. 인기도 점수 증가 및 실시간 알림 전송
     */
    public void incrementScore(String nickname) {
        // 1. Redis 점수 업데이트
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, nickname, 1.0);

        // 2. [핵심] 점수가 바뀌었으므로 최신 랭킹을 가져와서 구독자들에게 실시간 전송!
        List<String> updatedRanking = getTopRanking();
        messagingTemplate.convertAndSend("/sub/ranking", updatedRanking);
    }

    /**
     * 2. 실시간 TOP 5 상담사 조회
     */
    public List<String> getTopRanking() {
        Set<Object> topReaders = redisTemplate.opsForZSet().reverseRange(RANKING_KEY, 0, 4);

        if (topReaders == null) return List.of();

        return topReaders.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}