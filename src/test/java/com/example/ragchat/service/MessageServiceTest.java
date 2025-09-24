package com.example.ragchat.service;

import com.example.ragchat.domain.ChatMessage;
import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.repo.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MessageServiceTest {

    private ChatMessageRepository repository;
    private MessageService service;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(ChatMessageRepository.class);
        service = new MessageService(repository);
    }

    @Test
    void addMessage_persistsEntity() {
        ChatSession session = ChatSession.builder().userId("u").title("t").build();
        when(repository.save(any(ChatMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ChatMessage m = service.addMessage(session, "USER", "hi", null);
        assertThat(m.getSender()).isEqualTo("USER");
        assertThat(m.getContent()).isEqualTo("hi");
    }

    @Test
    void listMessages_returnsPage() {
        when(repository.findBySessionOrderByCreatedAtAsc(any(), any())).thenReturn(new PageImpl<>(List.of()));
        Page<ChatMessage> page = service.listMessages(ChatSession.builder().build(), 0, 10);
        assertThat(page.getContent()).isEmpty();
    }
}


