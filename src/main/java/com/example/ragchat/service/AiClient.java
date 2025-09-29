package com.example.ragchat.service;

import java.util.List;

public interface AiClient {

    String generateAssistantReply(String systemPrompt, List<ChatMessagePart> historyParts);

    class ChatMessagePart {
        public final String role; // "user" or "assistant"
        public final String content;

        public ChatMessagePart(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}


