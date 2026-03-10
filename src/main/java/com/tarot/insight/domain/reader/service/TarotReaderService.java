package com.tarot.insight.domain.reader.service;

import com.tarot.insight.domain.reader.dto.ReaderSearchCondition;
import com.tarot.insight.domain.reader.dto.TarotReaderResponse;
import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TarotReaderService {

    private final TarotReaderRepository tarotReaderRepository;

    /**
     * 1. 전체 상담사 목록 조회 (캐시 적용)
     */
    @Cacheable(value = "readers", key = "'all'", cacheManager = "cacheManager")
    @Transactional(readOnly = true)
    public TarotReaderResponse[] getAllReaders() {
        log.info(">>>> [DB 직접 조회] Redis에 데이터가 없어 MySQL에서 상담사 목록을 가져옵니다.");

        return tarotReaderRepository.findAllByIsActiveTrue()
                .stream()
                .map(TarotReaderResponse::new)
                .toArray(TarotReaderResponse[]::new);
    }

    /**
     * 2. 상담사 프로필 수정 (캐시 삭제 로직 포함)
     * 이 메서드가 실행되면 Redis에 저장된 'readers::all' 캐시가 자동으로 삭제.
     */
    @CacheEvict(value = "readers", key = "'all'") // 데이터가 바뀌면 캐시를 비움.
    @Transactional
    public void updateReaderProfile(Long readerId, String newProfile, int years) {
        log.info(">>>> [프로필 수정] 상담사 ID {}의 정보를 수정하고 관련 캐시를 삭제합니다.", readerId);

        TarotReader reader = tarotReaderRepository.findById(readerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담사입니다."));

        // 데이터 업데이트 (Dirty Checking으로 자동 반영)
        reader.updateProfile(newProfile, years);
    }

    /**
     * 3. 상담사 동적 검색 (QueryDSL 활용)
     * 닉네임, 최소 경력, 최소 평점 등 복합 조건으로 검색합니다.
     * 동적 검색은 조건이 매번 다르므로 보통 캐싱을 적용하지 않습니다.
     */
    @Transactional(readOnly = true)
    public List<TarotReaderResponse> searchReaders(ReaderSearchCondition condition) {
        log.info(">>>> [상담사 검색] 조건: 닉네임={}, 경력={}년 이상, 평점={}점 이상",
                condition.getNickname(), condition.getMinExperience(), condition.getMinRating());

        // QueryDSL Custom Repository 메서드 호출
        return tarotReaderRepository.searchReaders(condition)
                .stream()
                .map(TarotReaderResponse::new)
                .toList();
    }
}