package com.example.ragchat.web;

import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.service.SessionService;
import com.example.ragchat.web.dto.SessionDtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions")
public class SessionController {
    private final SessionService sessionService;

    @PostMapping
    @Operation(summary = "Create a chat session")
    public ResponseEntity<SessionResponse> create(@Valid @RequestBody CreateSessionRequest req) {
        ChatSession s = sessionService.createSession(req.userId(), req.title());
        return ResponseEntity.ok(new SessionResponse(s.getId(), s.getUserId(), s.getTitle(), s.isFavorite()));
    }

    @GetMapping
    @Operation(summary = "List chat sessions for a user")
    public ResponseEntity<Page<ChatSession>> list(@RequestParam String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(sessionService.listSessions(userId, page, size));
    }

    @PatchMapping("/{id}/title")
    @Operation(summary = "Rename a chat session")
    public ResponseEntity<SessionResponse> rename(@PathVariable UUID id, @RequestParam String userId, @Valid @RequestBody RenameSessionRequest req) {
        ChatSession s = sessionService.rename(id, userId, req.title());
        return ResponseEntity.ok(new SessionResponse(s.getId(), s.getUserId(), s.getTitle(), s.isFavorite()));
    }

    @PatchMapping("/{id}/favorite")
    @Operation(summary = "Toggle favorite flag")
    public ResponseEntity<SessionResponse> favorite(@PathVariable UUID id, @RequestParam String userId, @Valid @RequestBody ToggleFavoriteRequest req) {
        ChatSession s = sessionService.toggleFavorite(id, userId, req.favorite());
        return ResponseEntity.ok(new SessionResponse(s.getId(), s.getUserId(), s.getTitle(), s.isFavorite()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete session and its messages")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestParam String userId) {
        sessionService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}


