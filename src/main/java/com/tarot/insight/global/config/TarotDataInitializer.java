package com.tarot.insight.global.config;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import com.tarot.insight.domain.tarot.entity.TarotCard;
import com.tarot.insight.domain.tarot.repository.TarotCardRepository;
import com.tarot.insight.domain.user.User;
import com.tarot.insight.domain.user.UserRepository;
import com.tarot.insight.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TarotDataInitializer implements CommandLineRunner {

    private final TarotCardRepository tarotCardRepository;
    private final UserRepository userRepository;
    private final TarotReaderRepository tarotReaderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional // 데이터 정합성을 위해 트랜잭션 권장
    public void run(String... args) {

        // 1. 타로 카드 초기화 (카드가 없을 때만 실행)
        if (tarotCardRepository.count() == 0) {
            TarotCard fool = TarotCard.builder()
                    .name("The Fool")
                    .description("모험, 순수, 시작을 의미합니다.")
                    .imageUrl("https://example.com/cards/0_fool.png")
                    .build();
            // ... 나머지 카드들 ...
            tarotCardRepository.saveAll(Arrays.asList(fool /* , magician, priestess */));
            System.out.println("✅ 타로 카드 데이터 삽입 완료");
        }

        // 3. 상담사 유저 '앨리스' (READER 권한)
        User aliceUser = userRepository.findByEmail("alice@tarot.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .email("alice@tarot.com")
                        .password(passwordEncoder.encode("1234"))
                        .nickname("상담사 앨리스")
                        .role(UserRole.READER) // ✅ 기존 Enum 사용
                        .build()));


        // 3. 앨리스 상담사 프로필 등록 (상담사 프로필이 없을 때만 실행)
        if (tarotReaderRepository.count() == 0) {
            tarotReaderRepository.save(TarotReader.builder()
                    .user(aliceUser)
                    .profile("15년 경력의 베테랑 상담사 앨리스입니다.")
                    .experienceYears(15)
                    .rating(4.9)
                    .isActive(true)
                    .build());
            System.out.println("✅ 상담사 앨리스(alice@tarot.com) 프로필 등록 완료");
        }

        System.out.println("🏁 모든 테스트 데이터 체크 완료");
    }
}