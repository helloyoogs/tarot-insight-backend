package com.tarot.insight.domain.tarot.service;

import com.tarot.insight.domain.tarot.dto.TarotReadingRequest;
import com.tarot.insight.domain.tarot.dto.TarotReadingResponse;
import com.tarot.insight.domain.tarot.entity.TarotCard;
import com.tarot.insight.domain.tarot.entity.TarotReading;
import com.tarot.insight.domain.tarot.repository.TarotCardRepository;
import com.tarot.insight.domain.tarot.repository.TarotReadingRepository;
import com.tarot.insight.domain.user.entity.User;
import com.tarot.insight.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TarotService {

    private final TarotReadingRepository tarotReadingRepository;
    private final TarotCardRepository tarotCardRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long saveReading(String email, TarotReadingRequest request) {
        // 1. 이메일로 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 2. 카드 ID로 카드 정보 찾기
        TarotCard card = tarotCardRepository.findById(request.getCardId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드입니다."));

        // 3. 기록 저장
        TarotReading reading = TarotReading.builder()
                .user(user)
                .tarotCard(card)
                .question(request.getQuestion())
                .resultText(request.getResultText())
                .build();

        return tarotReadingRepository.save(reading).getId();
    }

    @Transactional(readOnly = true)
    public List<TarotReadingResponse> getMyHistory(String email) {
        // 1. 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 2. 해당 유저의 기록들 가져오기 (Repository에서 만든 메서드 활용)
        // 3. Entity 리스트를 DTO 리스트로 변환해서 반환 (Stream API 활용)
        return tarotReadingRepository.findAllByUserId(user.getId())
                .stream()
                .map(TarotReadingResponse::new)
                .toList();
    }
}