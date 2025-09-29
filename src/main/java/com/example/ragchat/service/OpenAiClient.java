package com.example.ragchat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient implements AiClient {

    @Value("${ai.openai.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.openai.model:gpt-4o-mini}")
    private String model;

    @Value("${ai.openai.api-key:}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    @Override
    public String generateAssistantReply(String systemPrompt, List<ChatMessagePart> historyParts) {
        try {
            var root = objectMapper.createObjectNode();
            root.put("model", model);
            var messages = objectMapper.createArrayNode();
            if (systemPrompt != null && !systemPrompt.isBlank()) {
                var sys = objectMapper.createObjectNode();
                sys.put("role", "system");
                sys.put("content", systemPrompt);
                messages.add(sys);
            }
            for (ChatMessagePart part : historyParts) {
                var node = objectMapper.createObjectNode();
                node.put("role", part.role);
                node.put("content", part.content);
                messages.add(node);
            }
            root.set("messages", messages);
            root.put("temperature", 0.2);

            byte[] bodyBytes = objectMapper.writeValueAsString(root).getBytes(StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(60))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode json = objectMapper.readTree(response.body());
                JsonNode messageNode = json.path("choices").path(0).path("message").path("content");
                String content = messageNode.isMissingNode() ? null : messageNode.asText();
                return content != null ? content : "";
            } else {
                log.warn("openai_error status={} body={} ", response.statusCode(), response.body());
                return "";
            }
        } catch (Exception e) {
            log.error("openai_exception", e);
            return "";
        }
    }
}


