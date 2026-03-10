package com.tarot.insight.domain.reader.repository;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.dto.ReaderSearchCondition;
import java.util.List;

public interface TarotReaderRepositoryCustom {
    List<TarotReader> searchReaders(ReaderSearchCondition condition);
}