package com.example.ragchat.web.mapper;

import com.example.ragchat.domain.ChatSession;
import com.example.ragchat.web.dto.SessionDtos.SessionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SessionMapper {
    SessionResponse toResponse(ChatSession session);
}


