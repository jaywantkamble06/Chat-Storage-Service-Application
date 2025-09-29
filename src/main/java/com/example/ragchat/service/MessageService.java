package com.example.ragchat.service;

import com.example.ragchat.domain.ChatMessage;
import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.repo.ChatMessageRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final ChatMessageRepository messageRepository;

    public ChatMessage addMessage(ChatSession session, String sender, String content, String context) {
        log.debug("saving_message session_id={} sender={} content_length={} has_context={}",
                session.getId(), sender, content != null ? content.length() : 0, context != null && !context.isBlank());
        ChatMessage msg = ChatMessage.builder()
                .session(session)
                .sender(sender)
                .content(content)
                .context(context)
                .build();
        ChatMessage saved = messageRepository.save(msg);
        log.info("message_saved id={} session_id={} sender={} ", saved.getId(), session.getId(), sender);
        return saved;
    }

    public Page<ChatMessage> listMessages(ChatSession session, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.debug("list_messages_repo session_id={} page={} size={}", session.getId(), page, size);
        return messageRepository.findBySessionOrderByCreatedAtAsc(session, pageable);
    }
}


