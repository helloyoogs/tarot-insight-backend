package com.tarot.insight.domain.chat.controller;

import com.tarot.insight.domain.chat.entity.ChatMessageEntity;
import com.tarot.insight.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatApiController {

    private final ChatService chatService;

    /**
     * 특정 채팅방의 이전 대화 내역 조회
     * GET /api/chat/room/1
     */
    @GetMapping("/room/{roomId}")
    public List<ChatMessageEntity> getChatHistory(@PathVariable String roomId) {
        return chatService.getChatHistory(roomId);
    }
}