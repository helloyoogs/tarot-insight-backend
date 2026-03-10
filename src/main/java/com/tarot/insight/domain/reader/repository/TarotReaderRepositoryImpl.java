package com.tarot.insight.domain.reader.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tarot.insight.domain.reader.dto.ReaderSearchCondition;
import com.tarot.insight.domain.reader.entity.TarotReader;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

// Q클래스 임포트 확인 (컴파일 후 생성되어야 함)
import static com.tarot.insight.domain.reader.entity.QTarotReader.tarotReader;
import static com.tarot.insight.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TarotReaderRepositoryImpl implements TarotReaderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TarotReader> searchReaders(ReaderSearchCondition condition) {
        return queryFactory
                .selectFrom(tarotReader)
                .join(tarotReader.user, user).fetchJoin()
                .where(
                        nicknameContains(condition.getNickname()),
                        experienceGoe(condition.getMinExperience()),
                        ratingGoe(condition.getMinRating()),
                        tarotReader.isActive.eq(true)
                )
                .orderBy(tarotReader.rating.desc(), tarotReader.experienceYears.desc())
                .fetch();
    }

    // --- 동적 쿼리용 BooleanExpression 조각들 ---

    private BooleanExpression nicknameContains(String nickname) {
        return StringUtils.hasText(nickname) ? user.nickname.contains(nickname) : null;
    }

    private BooleanExpression experienceGoe(Integer minExperience) {
        return minExperience != null ? tarotReader.experienceYears.goe(minExperience) : null;
    }

    private BooleanExpression ratingGoe(Double minRating) {
        return minRating != null ? tarotReader.rating.goe(minRating) : null;
    }
}