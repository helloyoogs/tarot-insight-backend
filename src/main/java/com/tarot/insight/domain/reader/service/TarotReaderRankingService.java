package com.tarot.insight.domain.reader.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TarotReaderRankingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String RANKING_KEY = "reader:ranking";

    /**
     * 1. 인기도 점수 증가 (상담사의 닉네임을 키로 점수 누적)
     */
    public void incrementScore(String nickname) {
        // 해당 닉네임의 점수를 1점 올립니다. (데이터가 없으면 새로 생성)
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, nickname, 1.0);
    }

    /**
     * 2. 실시간 TOP 5 상담사 조회
     */
    public List<String> getTopRanking() {
        // 점수가 높은 순(Reverse Range)으로 0~4위까지 가져옵니다.
        Set<Object> topReaders = redisTemplate.opsForZSet().reverseRange(RANKING_KEY, 0, 4);

        return topReaders.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}