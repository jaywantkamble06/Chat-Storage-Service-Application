package com.example.ragchat.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class MessageDtos {
    public record AddMessageRequest(
            @NotBlank String sender,
            @NotBlank String content,
            @Size(max = 10000) String context
    ) {}

    public record MessageResponse(
            UUID id,
            String sender,
            String content,
            String context,
            String createdAt
    ) {}
}


