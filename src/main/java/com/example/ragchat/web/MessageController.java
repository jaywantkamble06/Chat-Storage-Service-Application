package com.example.ragchat.web;

import com.example.ragchat.domain.ChatMessage;
import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.service.MessageService;
import com.example.ragchat.service.SessionService;
import com.example.ragchat.web.dto.MessageDtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions/{sessionId}/messages")
@RequiredArgsConstructor
@Tag(name = "Messages")
public class MessageController {
    private final MessageService messageService;
    private final SessionService sessionService;

    @PostMapping
    @Operation(summary = "Add a message to a session")
    public ResponseEntity<MessageResponse> add(@PathVariable UUID sessionId, @RequestParam String userId, @Valid @RequestBody AddMessageRequest req) {
        ChatSession s = sessionService.getOwned(sessionId, userId);
        ChatMessage m = messageService.addMessage(s, req.sender(), req.content(), req.context());
        return ResponseEntity.ok(new MessageResponse(m.getId(), m.getSender(), m.getContent(), m.getContext(), DateTimeFormatter.ISO_INSTANT.format(m.getCreatedAt())));
    }

    @GetMapping
    @Operation(summary = "List messages in a session (paginated)")
    public ResponseEntity<Page<ChatMessage>> list(@PathVariable UUID sessionId, @RequestParam String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        ChatSession s = sessionService.getOwned(sessionId, userId);
        return ResponseEntity.ok(messageService.listMessages(s, page, size));
    }
}


