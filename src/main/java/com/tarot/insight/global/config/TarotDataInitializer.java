package com.tarot.insight.global.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import com.tarot.insight.domain.tarot.entity.TarotCard;
import com.tarot.insight.domain.tarot.entity.TarotDeck;
import com.tarot.insight.domain.tarot.entity.TarotInterpretation;
import com.tarot.insight.domain.tarot.entity.TarotTheme;
import com.tarot.insight.domain.tarot.repository.TarotCardRepository;
import com.tarot.insight.domain.tarot.repository.TarotDeckRepository;
import com.tarot.insight.domain.tarot.repository.TarotInterpretationRepository;
import com.tarot.insight.domain.tarot.repository.TarotThemeRepository;
import com.tarot.insight.domain.user.entity.User;
import com.tarot.insight.domain.user.entity.UserRole;
import com.tarot.insight.domain.user.repository.UserRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TarotDataInitializer implements CommandLineRunner {

    private final TarotCardRepository tarotCardRepository;
    private final TarotThemeRepository tarotThemeRepository;
    private final TarotDeckRepository tarotDeckRepository;
    private final TarotInterpretationRepository tarotInterpretationRepository;
    private final UserRepository userRepository;
    private final TarotReaderRepository tarotReaderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional // 데이터 정합성을 위해 트랜잭션 권장
    public void run(String... args) {

        initializeTarotMasterData();

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

    /**
     * 카드/테마/덱/해석 초기 데이터 주입
     * - 이미 데이터가 있으면 아무 것도 하지 않음
     */
    private void initializeTarotMasterData() {
        // 1. 테마 마스터
        tarotThemeRepository.findByThemeCode("LOVE")
                .orElseGet(() -> tarotThemeRepository.save(
                        TarotTheme.builder().themeCode("LOVE").themeName("연애운").build()
                ));
        tarotThemeRepository.findByThemeCode("JOB")
                .orElseGet(() -> tarotThemeRepository.save(
                        TarotTheme.builder().themeCode("JOB").themeName("직업운").build()
                ));
        tarotThemeRepository.findByThemeCode("SUCCESS")
                .orElseGet(() -> tarotThemeRepository.save(
                        TarotTheme.builder().themeCode("SUCCESS").themeName("성공운").build()
                ));
        tarotThemeRepository.findByThemeCode("STUDY")
                .orElseGet(() -> tarotThemeRepository.save(
                        TarotTheme.builder().themeCode("STUDY").themeName("학업운").build()
                ));
        tarotThemeRepository.findByThemeCode("WEALTH")
                .orElseGet(() -> tarotThemeRepository.save(
                        TarotTheme.builder().themeCode("WEALTH").themeName("재물운").build()
                ));
        tarotThemeRepository.findByThemeCode("HEALTH")
                .orElseGet(() -> tarotThemeRepository.save(
                        TarotTheme.builder().themeCode("HEALTH").themeName("건강운").build()
                ));
        tarotThemeRepository.findByThemeCode("RELATION")
                .orElseGet(() -> tarotThemeRepository.save(
                        TarotTheme.builder().themeCode("RELATION").themeName("관계운").build()
                ));
        tarotThemeRepository.findByThemeCode("TODAY")
                .orElseGet(() -> tarotThemeRepository.save(
                        TarotTheme.builder().themeCode("TODAY").themeName("오늘운").build()
                ));

        // 2. 덱 마스터
        tarotDeckRepository.findByDeckCode("ROMANTIC")
                .orElseGet(() -> tarotDeckRepository.save(
                        TarotDeck.builder().deckCode("ROMANTIC").deckName("Romantic Tarot").build()
                ));
        tarotDeckRepository.findByDeckCode("WAITE")
                .orElseGet(() -> tarotDeckRepository.save(
                        TarotDeck.builder().deckCode("WAITE").deckName("Waite Tarot").build()
                ));
        tarotDeckRepository.findByDeckCode("GOLDEN")
                .orElseGet(() -> tarotDeckRepository.save(
                        TarotDeck.builder().deckCode("GOLDEN").deckName("Golden Tarot").build()
                ));
        tarotDeckRepository.findByDeckCode("WHITE_CATS")
                .orElseGet(() -> tarotDeckRepository.save(
                        TarotDeck.builder().deckCode("WHITE_CATS").deckName("White Cats Tarot").build()
                ));
        tarotDeckRepository.findByDeckCode("GILDED")
                .orElseGet(() -> tarotDeckRepository.save(
                        TarotDeck.builder().deckCode("GILDED").deckName("Gilded Tarot").build()
                ));
        tarotDeckRepository.findByDeckCode("HERBAL")
                .orElseGet(() -> tarotDeckRepository.save(
                        TarotDeck.builder().deckCode("HERBAL").deckName("Herbal Tarot").build()
                ));
        tarotDeckRepository.findByDeckCode("SYMBOLON")
                .orElseGet(() -> tarotDeckRepository.save(
                        TarotDeck.builder().deckCode("SYMBOLON").deckName("Symbolon Tarot").build()
                ));
        tarotDeckRepository.findByDeckCode("MARSEILLE")
                .orElseGet(() -> tarotDeckRepository.save(
                        TarotDeck.builder().deckCode("MARSEILLE").deckName("Marseille Tarot").build()
                ));

        // 3. 카드 마스터
        if (tarotCardRepository.count() == 0) {
// === Major Arcana (0~21) ===
            tarotCardRepository.save(TarotCard.builder().cardNo(0).name("The Fool").description("새로운 시작, 순수함, 자유로운 영혼, 무모한 도전").imageUrl("/images/tarot/0.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(1).name("The Magician").description("창조력, 기술력, 잠재력 실현, 새로운 기술의 습득").imageUrl("/images/tarot/1.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(2).name("The High Priestess").description("직관, 무의식, 내면의 지혜, 신비로움과 정적인 힘").imageUrl("/images/tarot/2.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(3).name("The Empress").description("풍요, 모성애, 자연의 생명력, 물질적 풍족함").imageUrl("/images/tarot/3.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(4).name("The Emperor").description("권위, 통제, 구조, 책임감 있는 리더십").imageUrl("/images/tarot/4.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(5).name("The Hierophant").description("전통, 조언, 종교적 가르침, 사회적 관습 준수").imageUrl("/images/tarot/5.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(6).name("The Lovers").description("사랑, 조화, 관계, 선택의 기로에서의 결합").imageUrl("/images/tarot/6.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(7).name("The Chariot").description("승리, 의지력, 추진력, 장애물을 극복하는 전진").imageUrl("/images/tarot/7.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(8).name("Strength").description("내면의 힘, 인내, 부드러운 통제력, 용기").imageUrl("/images/tarot/8.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(9).name("The Hermit").description("성찰, 고독, 내면의 진리 탐구, 신중한 조언").imageUrl("/images/tarot/9.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(10).name("Wheel of Fortune").description("운명의 변화, 전환점, 행운, 피할 수 없는 흐름").imageUrl("/images/tarot/10.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(11).name("Justice").description("정의, 공정함, 진실, 인과응보, 논리적 결정").imageUrl("/images/tarot/11.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(12).name("The Hanged Man").description("일시 정지, 희생, 새로운 관점, 기다림의 미학").imageUrl("/images/tarot/12.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(13).name("Death").description("종결, 변화, 새로운 시작을 위한 죽음, 전환").imageUrl("/images/tarot/13.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(14).name("Temperance").description("절제, 균형, 중용, 감정의 정화와 조화").imageUrl("/images/tarot/14.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(15).name("The Devil").description("집착, 유혹, 속박, 물질적 욕망에 사로잡힘").imageUrl("/images/tarot/15.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(16).name("The Tower").description("급작스러운 변화, 붕괴, 재난, 깨달음을 위한 파괴").imageUrl("/images/tarot/16.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(17).name("The Star").description("희망, 영감, 치유, 긍정적인 미래에 대한 비전").imageUrl("/images/tarot/17.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(18).name("The Moon").description("불안, 혼란, 환상, 보이지 않는 적, 직관의 필요성").imageUrl("/images/tarot/18.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(19).name("The Sun").description("성공, 기쁨, 활력, 명확함, 축복받은 결과").imageUrl("/images/tarot/19.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(20).name("Judgement").description("부활, 보상, 결단, 과거의 결과에 대한 심판").imageUrl("/images/tarot/20.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(21).name("The World").description("완성, 통합, 성공적인 마무리, 더 넓은 세상으로의 확장").imageUrl("/images/tarot/21.png").build());

// === Wands (22~35) ===
            tarotCardRepository.save(TarotCard.builder().cardNo(22).name("Ace of Wands").description("새로운 기회, 열정의 시작, 창조적 에너지").imageUrl("/images/tarot/22.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(23).name("2 of Wands").description("계획, 선택, 미래를 향한 시야, 주도권 확보").imageUrl("/images/tarot/23.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(24).name("3 of Wands").description("확장, 협력의 결과, 기다림 끝에 오는 소식").imageUrl("/images/tarot/24.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(25).name("4 of Wands").description("축하, 안정, 안식처, 조화로운 결과와 환희").imageUrl("/images/tarot/25.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(26).name("5 of Wands").description("갈등, 경쟁, 혼란스러운 상황, 의견 대립").imageUrl("/images/tarot/26.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(27).name("6 of Wands").description("승리, 인정, 자신감, 대중의 환호와 성취").imageUrl("/images/tarot/27.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(28).name("7 of Wands").description("방어, 용기 있는 저항, 유리한 고지 점령").imageUrl("/images/tarot/28.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(29).name("8 of Wands").description("신속함, 빠른 전개, 갑작스러운 소식과 이동").imageUrl("/images/tarot/29.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(30).name("9 of Wands").description("인내, 마지막 경계, 상처를 딛고 일어서는 의지").imageUrl("/images/tarot/30.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(31).name("10 of Wands").description("과도한 책임감, 압박감, 무거운 짐, 번아웃 주의").imageUrl("/images/tarot/31.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(32).name("Page of Wands").description("호기심, 열정적인 제안, 새로운 소식을 전하는 청년").imageUrl("/images/tarot/32.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(33).name("Knight of Wands").description("모험, 충동적 행동, 도전적인 추진력").imageUrl("/images/tarot/33.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(34).name("Queen of Wands").description("당당함, 매력, 활기찬 성격, 사교적인 리더십").imageUrl("/images/tarot/34.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(35).name("King of Wands").description("비전가, 카리스마, 강력한 리더십, 사업적 수완").imageUrl("/images/tarot/35.png").build());

// === Cups (36~49) ===
            tarotCardRepository.save(TarotCard.builder().cardNo(36).name("Ace of Cups").description("사랑의 시작, 감정의 충만, 순수한 기쁨").imageUrl("/images/tarot/36.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(37).name("2 of Cups").description("결합, 파트너십, 조화로운 관계, 상호 이해").imageUrl("/images/tarot/37.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(38).name("3 of Cups").description("우정, 축하, 공동체의 즐거움, 풍요로운 교류").imageUrl("/images/tarot/38.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(39).name("4 of Cups").description("권태, 불만족, 기회를 놓침, 내면으로의 침잠").imageUrl("/images/tarot/39.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(40).name("5 of Cups").description("상실, 실망, 후회, 남은 것에 대한 미련").imageUrl("/images/tarot/40.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(41).name("6 of Cups").description("그리움, 과거의 추억, 순수했던 시절, 재회").imageUrl("/images/tarot/41.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(42).name("7 of Cups").description("환상, 지나친 상상, 선택 장애, 비현실적인 꿈").imageUrl("/images/tarot/42.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(43).name("8 of Cups").description("미련 없는 떠남, 포기, 새로운 길을 향한 여정").imageUrl("/images/tarot/43.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(44).name("9 of Cups").description("만족, 소원 성취, 정서적 풍요, 자존감의 회복").imageUrl("/images/tarot/44.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(45).name("10 of Cups").description("가족의 행복, 완벽한 조화, 영원한 사랑과 평화").imageUrl("/images/tarot/45.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(46).name("Page of Cups").description("예술적 영감, 감수성, 부드러운 소식, 상상력").imageUrl("/images/tarot/46.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(47).name("Knight of Cups").description("로맨티스트, 프러포즈, 감성적인 제안과 다가옴").imageUrl("/images/tarot/47.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(48).name("Queen of Cups").description("공감 능력, 자애로움, 직관적인 지혜, 치유의 힘").imageUrl("/images/tarot/48.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(49).name("King of Cups").description("감정의 통제, 너그러움, 포용력 있는 리더십").imageUrl("/images/tarot/49.png").build());

// === Swords (50~63) ===
            tarotCardRepository.save(TarotCard.builder().cardNo(50).name("Ace of Swords").description("명확한 판단, 지적 돌파구, 승리, 단호한 결단").imageUrl("/images/tarot/50.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(51).name("2 of Swords").description("막다른 길, 갈등 속의 균형, 회피하고 싶은 선택").imageUrl("/images/tarot/51.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(52).name("3 of Swords").description("비탄, 상처, 이별의 아픔, 정신적 고통").imageUrl("/images/tarot/52.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(53).name("4 of Swords").description("휴식, 요양, 명상, 재충전을 위한 멈춤").imageUrl("/images/tarot/53.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(54).name("5 of Swords").description("패배, 갈등의 상처, 비겁한 승리, 불필요한 논쟁").imageUrl("/images/tarot/54.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(55).name("6 of Swords").description("어려움을 벗어남, 이동, 점진적인 회복, 변화의 수용").imageUrl("/images/tarot/55.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(56).name("7 of Swords").description("눈속임, 배신, 전략적 행동, 홀로 감당하는 비밀").imageUrl("/images/tarot/56.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(57).name("8 of Swords").description("무력감, 스스로 만든 감옥, 고립, 제한된 시야").imageUrl("/images/tarot/57.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(58).name("9 of Swords").description("악몽, 과도한 걱정, 불면증, 심리적 압박감").imageUrl("/images/tarot/58.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(59).name("10 of Swords").description("파멸, 끝자락, 바닥을 친 상황, 이제 나아질 일만 남음").imageUrl("/images/tarot/59.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(60).name("Page of Swords").description("기민함, 경계, 지적 호기심, 날카로운 관찰자").imageUrl("/images/tarot/60.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(61).name("Knight of Swords").description("급진적인 돌진, 단호함, 비판적인 태도, 성급함").imageUrl("/images/tarot/61.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(62).name("Queen of Swords").description("냉철한 지성, 공정함, 독립심, 팩트 중심의 사고").imageUrl("/images/tarot/62.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(63).name("King of Swords").description("최고 권위자, 법과 질서, 지적인 통제력, 공정성").imageUrl("/images/tarot/63.png").build());

// === Pentacles (64~77) ===
            tarotCardRepository.save(TarotCard.builder().cardNo(64).name("Ace of Pentacles").description("물질적 기회, 번영의 시작, 재정적 이득, 실질적 성과").imageUrl("/images/tarot/64.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(65).name("2 of Pentacles").description("균형 잡기, 멀티태스킹, 변화에 대한 적응, 유연함").imageUrl("/images/tarot/65.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(66).name("3 of Pentacles").description("기술, 협업, 전문성 발휘, 초기 성과와 인정").imageUrl("/images/tarot/66.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(67).name("4 of Pentacles").description("소유욕, 인색함, 안전 제일주의, 변화 거부").imageUrl("/images/tarot/67.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(68).name("5 of Pentacles").description("경제적 빈곤, 소외, 고난의 시기, 도움의 필요성").imageUrl("/images/tarot/68.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(69).name("6 of Pentacles").description("자선, 베풂, 공정한 나눔, 금전적 도움의 수수").imageUrl("/images/tarot/69.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(70).name("7 of Pentacles").description("인내의 수확, 투자 결과 기다림, 중간 점검").imageUrl("/images/tarot/70.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(71).name("8 of Pentacles").description("장인정신, 꾸준한 노력, 기술 연마, 성실한 업무").imageUrl("/images/tarot/71.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(72).name("9 of Pentacles").description("풍요로운 자립, 여유, 우아함, 스스로 이룬 성공").imageUrl("/images/tarot/72.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(73).name("10 of Pentacles").description("유산, 가문의 안정, 장기적인 부, 완벽한 물질적 행복").imageUrl("/images/tarot/73.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(74).name("Page of Pentacles").description("학구열, 실무적 제안, 현실적인 목표 설정, 성실함").imageUrl("/images/tarot/74.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(75).name("Knight of Pentacles").description("우직함, 책임감, 보수적 태도, 느리지만 확실한 전진").imageUrl("/images/tarot/75.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(76).name("Queen of Pentacles").description("실용적 지혜, 물질적 안락함, 보살핌, 내실 있는 경영").imageUrl("/images/tarot/76.png").build());
            tarotCardRepository.save(TarotCard.builder().cardNo(77).name("King of Pentacles").description("경제적 거물, 성공한 사업가, 현실적 권위, 반석 위의 성공").imageUrl("/images/tarot/77.png").build());
        }

        // 4. 해석 데이터 (tarot_interpretation)
        //    - resources/tarot_interpretations.json 을 읽어서 초기 624개를 생성
        if (tarotInterpretationRepository.count() == 0) {
            loadTarotInterpretationsFromJson();
        }
    }

    private void loadTarotInterpretationsFromJson() {
        ClassPathResource resource = new ClassPathResource("tarot_interpretations.json");
        ObjectMapper objectMapper = new ObjectMapper();

        try (InputStream is = resource.getInputStream()) {
            List<TarotInterpretationSeed> seeds =
                    objectMapper.readValue(is, new TypeReference<List<TarotInterpretationSeed>>() {});

            for (TarotInterpretationSeed seed : seeds) {
                TarotTheme theme = tarotThemeRepository.findByThemeCode(seed.getThemeCode())
                        .orElseThrow(() -> new IllegalStateException("존재하지 않는 themeCode: " + seed.getThemeCode()));
                TarotDeck deck = tarotDeckRepository.findByDeckCode(seed.getDeckCode())
                        .orElseThrow(() -> new IllegalStateException("존재하지 않는 deckCode: " + seed.getDeckCode()));
                TarotCard card = tarotCardRepository.findByCardNo(seed.getCardNo())
                        .orElseThrow(() -> new IllegalStateException("존재하지 않는 cardNo: " + seed.getCardNo()));

                tarotInterpretationRepository.save(
                        TarotInterpretation.builder()
                                .theme(theme)
                                .deck(deck)
                                .tarotCard(card)
                                .resultText(seed.getResultText())
                                .build()
                );
            }
        } catch (IOException e) {
            throw new IllegalStateException("tarot_interpretations.json 로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * JSON 파싱용 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    private static class TarotInterpretationSeed {
        private String themeCode;
        private String deckCode;
        private Integer cardNo;
        private String resultText;
    }
}