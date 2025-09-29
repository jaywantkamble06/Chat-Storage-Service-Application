package com.example.ragchat.web;

import com.example.ragchat.domain.ChatMessage;
import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.service.AgentService;
import com.example.ragchat.service.SessionService;
import com.example.ragchat.web.dto.MessageDtos.MessageResponse;
import com.example.ragchat.web.mapper.MessageMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessions/{sessionId}/agent")
@RequiredArgsConstructor
@Tag(name = "Agent")
@Slf4j
public class AgentController {

    private final AgentService agentService;
    private final SessionService sessionService;
    private final MessageMapper messageMapper;

    @PostMapping("/reply")
    @Operation(summary = "Generate an assistant reply for the session")
    public ResponseEntity<MessageResponse> reply(@PathVariable UUID sessionId, @RequestParam String userId,
                                                 @RequestParam(required = false, defaultValue = "You are a helpful assistant.") String systemPrompt) {
        log.info("agent_reply session_id={} user_id={}", sessionId, userId);
        ChatSession s = sessionService.getOwned(sessionId, userId);
        ChatMessage m = agentService.generateAssistantMessage(s, systemPrompt);
        return ResponseEntity.ok(messageMapper.toResponse(m));
    }
}


