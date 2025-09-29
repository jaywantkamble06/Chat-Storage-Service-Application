package com.example.ragchat.service;

import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.repo.ChatSessionRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {
    private final ChatSessionRepository sessionRepository;

    public ChatSession createSession(String userId, String title) {
        log.debug("creating_session user_id={} title_length={}", userId, title != null ? title.length() : 0);
        ChatSession session = ChatSession.builder()
                .userId(userId)
                .title(title)
                .favorite(false)
                .build();
        ChatSession saved = sessionRepository.save(session);
        log.info("session_created id={} user_id={}", saved.getId(), userId);
        return saved;
    }

    public Page<ChatSession> listSessions(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.debug("list_sessions_repo user_id={} page={} size={}", userId, page, size);
        return sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable);
    }

    public ChatSession getOwned(UUID id, String userId) {
        return sessionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
    }

    public ChatSession rename(UUID id, String userId, String title) {
        ChatSession s = getOwned(id, userId);
        s.setTitle(title);
        ChatSession saved = sessionRepository.save(s);
        log.info("session_renamed id={} user_id={}", id, userId);
        return saved;
    }

    public ChatSession toggleFavorite(UUID id, String userId, boolean favorite) {
        ChatSession s = getOwned(id, userId);
        s.setFavorite(favorite);
        ChatSession saved = sessionRepository.save(s);
        log.info("session_favorite_toggled id={} user_id={} favorite={}", id, userId, favorite);
        return saved;
    }

    public void delete(UUID id, String userId) {
        ChatSession s = getOwned(id, userId);
        sessionRepository.delete(s);
        log.info("session_deleted id={} user_id={}", id, userId);
    }
}


