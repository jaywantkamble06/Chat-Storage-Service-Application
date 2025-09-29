package com.example.ragchat.web;

import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.service.SessionService;
import com.example.ragchat.web.dto.SessionDtos.*;
import com.example.ragchat.web.mapper.SessionMapper;
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
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions")
@Slf4j
public class SessionController {
    private final SessionService sessionService;
    private final SessionMapper sessionMapper;

    @PostMapping
    @Operation(summary = "Create a chat session")
    public ResponseEntity<SessionResponse> create(@Valid @RequestBody CreateSessionRequest req) {
        log.info("create_session user_id={} title_length={}", req.userId(), req.title() != null ? req.title().length() : 0);
        ChatSession s = sessionService.createSession(req.userId(), req.title());
        return ResponseEntity.ok(sessionMapper.toResponse(s));
    }

    @GetMapping
    @Operation(summary = "List chat sessions for a user")
    public ResponseEntity<Page<ChatSession>> list(@RequestParam String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        log.info("list_sessions user_id={} page={} size={}", userId, page, size);
        return ResponseEntity.ok(sessionService.listSessions(userId, page, size));
    }

    @PatchMapping("/{id}/title")
    @Operation(summary = "Rename a chat session")
    public ResponseEntity<SessionResponse> rename(@PathVariable UUID id, @RequestParam String userId, @Valid @RequestBody RenameSessionRequest req) {
        log.info("rename_session id={} user_id={} title_length={}", id, userId, req.title() != null ? req.title().length() : 0);
        ChatSession s = sessionService.rename(id, userId, req.title());
        return ResponseEntity.ok(sessionMapper.toResponse(s));
    }

    @PatchMapping("/{id}/favorite")
    @Operation(summary = "Toggle favorite flag")
    public ResponseEntity<SessionResponse> favorite(@PathVariable UUID id, @RequestParam String userId, @Valid @RequestBody ToggleFavoriteRequest req) {
        log.info("toggle_favorite id={} user_id={} favorite={}", id, userId, req.favorite());
        ChatSession s = sessionService.toggleFavorite(id, userId, req.favorite());
        return ResponseEntity.ok(sessionMapper.toResponse(s));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete session and its messages")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestParam String userId) {
        log.info("delete_session id={} user_id={} ", id, userId);
        sessionService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}


