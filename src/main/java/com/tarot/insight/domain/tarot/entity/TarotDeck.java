package com.tarot.insight.domain.tarot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tarot_deck")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TarotDeck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "deck_code", nullable = false, unique = true)
    private String deckCode; // ROMANTIC, WAITE, GOLDEN ...

    @Column(name = "deck_name", nullable = false)
    private String deckName; // 'Romantic Tarot' 등
}

