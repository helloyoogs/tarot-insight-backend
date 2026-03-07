package com.tarot.insight.global.config;

import com.tarot.insight.domain.tarot.entity.TarotCard;
import com.tarot.insight.domain.tarot.repository.TarotCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TarotDataInitializer implements CommandLineRunner {

    private final TarotCardRepository tarotCardRepository;

    @Override
    public void run(String... args) {
        // 이미 데이터가 있다면 중복으로 넣지 않도록 체크.
        if (tarotCardRepository.count() > 0) {
            return;
        }

        // 대표적인 메이저 아르카나 3장만.
        TarotCard fool = TarotCard.builder()
                .name("The Fool")
                .description("모험, 순수, 시작을 의미합니다. 새로운 여정이 당신을 기다리고 있네요.")
                .imageUrl("https://example.com/cards/0_fool.png")
                .build();

        TarotCard magician = TarotCard.builder()
                .name("The Magician")
                .description("창조, 능력, 실현을 의미합니다. 당신은 이미 준비가 되어 있습니다.")
                .imageUrl("https://example.com/cards/1_magician.png")
                .build();

        TarotCard priestess = TarotCard.builder()
                .name("The High Priestess")
                .description("직관, 신비, 내면의 목소리를 의미합니다. 잠시 멈춰 당신의 마음을 들여다보세요.")
                .imageUrl("https://example.com/cards/2_priestess.png")
                .build();

        tarotCardRepository.saveAll(Arrays.asList(fool, magician, priestess));
        System.out.println("✅ 타로 카드 테스트 데이터 삽입 완료!");
    }
}