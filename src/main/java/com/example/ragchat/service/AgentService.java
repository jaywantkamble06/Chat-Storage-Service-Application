package com.example.ragchat.service;

import com.example.ragchat.domain.ChatMessage;
import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.repo.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentService {

    private final AiClient aiClient;
    private final ChatMessageRepository messageRepository;

    public ChatMessage generateAssistantMessage(ChatSession session, String systemPrompt) {
        List<ChatMessage> recent = messageRepository.findBySessionOrderByCreatedAtAsc(session, PageRequest.of(0, 50)).getContent();
        List<AiClient.ChatMessagePart> parts = new ArrayList<>();
        for (ChatMessage m : recent) {
            String role = "assistant".equalsIgnoreCase(m.getSender()) ? "assistant" : "user";
            parts.add(new AiClient.ChatMessagePart(role, m.getContent()));
        }
        String reply = aiClient.generateAssistantReply(systemPrompt, parts);
        ChatMessage saved = messageRepository.save(ChatMessage.builder()
                .session(session)
                .sender("ASSISTANT")
                .content(reply == null ? "" : reply)
                .build());
        log.info("agent_generated_message session_id={} message_id={}", session.getId(), saved.getId());
        return saved;
    }
}


