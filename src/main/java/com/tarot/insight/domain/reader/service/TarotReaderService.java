package com.tarot.insight.domain.reader.service;

import com.tarot.insight.domain.reader.dto.TarotReaderResponse;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TarotReaderService {

    private final TarotReaderRepository tarotReaderRepository;

    @Transactional(readOnly = true)
    public List<TarotReaderResponse> getAllReaders() {
        return tarotReaderRepository.findAllByIsActiveTrue()
                .stream()
                .map(TarotReaderResponse::new)
                .toList();
    }
}