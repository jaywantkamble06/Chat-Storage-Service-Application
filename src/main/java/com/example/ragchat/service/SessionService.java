package com.example.ragchat.service;

import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.repo.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final ChatSessionRepository sessionRepository;

    public ChatSession createSession(String userId, String title) {
        ChatSession session = ChatSession.builder()
                .userId(userId)
                .title(title)
                .favorite(false)
                .build();
        return sessionRepository.save(session);
    }

    public Page<ChatSession> listSessions(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable);
    }

    public ChatSession getOwned(UUID id, String userId) {
        return sessionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
    }

    public ChatSession rename(UUID id, String userId, String title) {
        ChatSession s = getOwned(id, userId);
        s.setTitle(title);
        return sessionRepository.save(s);
    }

    public ChatSession toggleFavorite(UUID id, String userId, boolean favorite) {
        ChatSession s = getOwned(id, userId);
        s.setFavorite(favorite);
        return sessionRepository.save(s);
    }

    public void delete(UUID id, String userId) {
        ChatSession s = getOwned(id, userId);
        sessionRepository.delete(s);
    }
}


