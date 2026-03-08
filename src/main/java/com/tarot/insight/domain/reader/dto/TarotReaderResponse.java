package com.tarot.insight.domain.reader.dto;

import com.tarot.insight.domain.reader.entity.TarotReader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor; // [추가]

@Getter
@NoArgsConstructor // Redis(Jackson)가 객체를 생성할 때 필요.
@AllArgsConstructor // 모든 필드를 사용하는 생성자 자동 생성
public class TarotReaderResponse {
    private Long id;
    private String nickname;
    private String profile;
    private int experienceYears;
    private Double rating;

    // 기존에 사용하시던 Entity -> DTO 변환 생성자.
    public TarotReaderResponse(TarotReader reader) {
        this.id = reader.getId();
        this.nickname = reader.getUser().getNickname();
        this.profile = reader.getProfile();
        this.experienceYears = reader.getExperienceYears();
        this.rating = reader.getRating();
    }
}