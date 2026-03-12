package com.tarot.insight.domain.reader.dto;

import com.tarot.insight.domain.reader.entity.TarotReader;
import lombok.Getter;

@Getter
public class TarotReaderAdminResponse {

    private final Long id;
    private final String nickname;
    private final String profile;
    private final int experienceYears;
    private final Double rating;
    private final boolean active;

    public TarotReaderAdminResponse(TarotReader reader) {
        this.id = reader.getId();
        this.nickname = reader.getUser().getNickname();
        this.profile = reader.getProfile();
        this.experienceYears = reader.getExperienceYears();
        this.rating = reader.getRating();
        this.active = reader.isActive();
    }
}

