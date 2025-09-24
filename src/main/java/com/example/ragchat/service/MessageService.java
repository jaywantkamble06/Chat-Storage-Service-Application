package com.example.ragchat.service;

import com.example.ragchat.domain.ChatMessage;
import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.repo.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final ChatMessageRepository messageRepository;

    public ChatMessage addMessage(ChatSession session, String sender, String content, String context) {
        ChatMessage msg = ChatMessage.builder()
                .session(session)
                .sender(sender)
                .content(content)
                .context(context)
                .build();
        return messageRepository.save(msg);
    }

    public Page<ChatMessage> listMessages(ChatSession session, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findBySessionOrderByCreatedAtAsc(session, pageable);
    }
}


