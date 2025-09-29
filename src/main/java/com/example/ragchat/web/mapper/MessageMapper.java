package com.example.ragchat.web.mapper;

import com.example.ragchat.domain.ChatMessage;
import com.example.ragchat.web.dto.MessageDtos.MessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "toIso")
    MessageResponse toResponse(ChatMessage message);

    @Named("toIso")
    static String toIso(java.time.Instant instant) {
        return instant != null ? DateTimeFormatter.ISO_INSTANT.format(instant) : null;
    }
}


