package com.example.ragchat.web;

import com.example.ragchat.domain.ChatMessage;
import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.service.MessageService;
import com.example.ragchat.service.SessionService;
import com.example.ragchat.web.dto.MessageDtos.*;
import com.example.ragchat.web.mapper.MessageMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessions/{sessionId}/messages")
@RequiredArgsConstructor
@Tag(name = "Messages")
@Slf4j
public class MessageController {
    private final MessageService messageService;
    private final SessionService sessionService;
    private final MessageMapper messageMapper;

    @PostMapping
    @Operation(summary = "Add a message to a session")
    public ResponseEntity<MessageResponse> add(@PathVariable("sessionId") UUID sessionId, @RequestParam("userId") String userId, @Valid @RequestBody AddMessageRequest req) {
        log.info("add_message session_id={} user_id={} sender={} has_context={}", sessionId, userId, req.sender(), req.context() != null && !req.context().isBlank());
        ChatSession s = sessionService.getOwned(sessionId, userId);
        ChatMessage m = messageService.addMessage(s, req.sender(), req.content(), req.context());
        return ResponseEntity.ok(messageMapper.toResponse(m));
    }

    @GetMapping
    @Operation(summary = "List messages in a session (paginated)")
    public ResponseEntity<Page<ChatMessage>> list(@PathVariable("sessionId") UUID sessionId, @RequestParam("userId") String userId, @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "50") int size) {
        log.info("list_messages session_id={} user_id={} page={} size={}", sessionId, userId, page, size);
        ChatSession s = sessionService.getOwned(sessionId, userId);
        return ResponseEntity.ok(messageService.listMessages(s, page, size));
    }
}


