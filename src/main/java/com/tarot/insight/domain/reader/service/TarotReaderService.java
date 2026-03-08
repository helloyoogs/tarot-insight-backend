package com.tarot.insight.domain.reader.service;

import com.tarot.insight.domain.reader.dto.TarotReaderResponse;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TarotReaderService {

    private final TarotReaderRepository tarotReaderRepository;

    /**
     * 전체 상담사 목록 조회 (캐시 적용)
     * 반환 타입을 List에서 배열(TarotReaderResponse[])로 바꿉니다.
     */
    @Cacheable(value = "readers", key = "'all'", cacheManager = "cacheManager")
    @Transactional(readOnly = true)
    public TarotReaderResponse[] getAllReaders() { // [수정] List -> 배열로 변경
        log.info(">>>> [DB 직접 조회] Redis에 데이터가 없어 MySQL에서 상담사 목록을 가져옵니다.");

        return tarotReaderRepository.findAllByIsActiveTrue()
                .stream()
                .map(TarotReaderResponse::new)
                .toArray(TarotReaderResponse[]::new); // [수정] 배열로 변환해서 반환
    }
}