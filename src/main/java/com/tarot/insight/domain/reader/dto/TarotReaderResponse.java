package com.tarot.insight.domain.reader.dto;

import com.tarot.insight.domain.reader.entity.TarotReader;
import lombok.Getter;

@Getter
public class TarotReaderResponse {
    private Long id;
    private String nickname;      // User 테이블에서 가져올 정보
    private String profile;       // Reader 테이블 정보
    private int experienceYears;
    private Double rating;

    public TarotReaderResponse(TarotReader reader) {
        this.id = reader.getId();
        this.nickname = reader.getUser().getNickname();
        this.profile = reader.getProfile();
        this.experienceYears = reader.getExperienceYears();
        this.rating = reader.getRating();
    }
}