package com.example.ragchat.service;

import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.repo.ChatSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SessionServiceTest {

    private ChatSessionRepository repository;
    private SessionService service;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(ChatSessionRepository.class);
        service = new SessionService(repository);
    }

    @Test
    void createSession_savesWithDefaults() {
        when(repository.save(any(ChatSession.class))).thenAnswer(invocation -> {
            ChatSession s = invocation.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });
        ChatSession s = service.createSession("user-1", "My Chat");
        assertThat(s.getId()).isNotNull();
        assertThat(s.getUserId()).isEqualTo("user-1");
        assertThat(s.getTitle()).isEqualTo("My Chat");
        assertThat(s.isFavorite()).isFalse();
    }

    @Test
    void listSessions_usesRepository() {
        when(repository.findByUserIdOrderByUpdatedAtDesc(eq("u"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));
        Page<ChatSession> page = service.listSessions("u", 0, 10);
        assertThat(page.getTotalElements()).isZero();
    }

    @Test
    void rename_updatesTitle() {
        UUID id = UUID.randomUUID();
        ChatSession existing = ChatSession.builder().id(id).userId("u").title("old").favorite(false).build();
        when(repository.findByIdAndUserId(id, "u")).thenReturn(Optional.of(existing));
        when(repository.save(any(ChatSession.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ChatSession updated = service.rename(id, "u", "new");
        assertThat(updated.getTitle()).isEqualTo("new");
    }

    @Test
    void getOwned_throwsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndUserId(id, "u")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.getOwned(id, "u"));
    }
}


