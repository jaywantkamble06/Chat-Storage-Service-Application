package com.example.ragchat.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class SessionDtos {
    public record CreateSessionRequest(
            @NotBlank String userId,
            @NotBlank @Size(max = 120) String title
    ) {}

    public record RenameSessionRequest(
            @NotBlank @Size(max = 120) String title
    ) {}

    public record ToggleFavoriteRequest(
            boolean favorite
    ) {}

    public record SessionResponse(
            UUID id,
            String userId,
            String title,
            boolean favorite
    ) {}
}


