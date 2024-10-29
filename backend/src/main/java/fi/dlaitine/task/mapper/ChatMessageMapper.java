package fi.dlaitine.task.mapper;

import fi.dlaitine.task.dto.ChatMessageResponseDto;
import fi.dlaitine.task.dto.CreateChatMessageDto;
import fi.dlaitine.task.entity.ChatMessageEntity;
import java.util.List;

public class ChatMessageMapper {

    private ChatMessageMapper() {

    }

    public static List<ChatMessageResponseDto> fromChatMessageEntityList(List<ChatMessageEntity> entities) {
        return entities.stream()
                .map(ChatMessageMapper::fromChatMessageEntity)
                .toList();
    }

    public static ChatMessageResponseDto fromChatMessageEntity(ChatMessageEntity entity) {
        return ChatMessageResponseDto.builder()
                .id(entity.getId())
                .userName(entity.getUserName())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static ChatMessageEntity fromCreateChatMessage(CreateChatMessageDto message) {
        return ChatMessageEntity.builder()
                .userName(message.userName())
                .content(message.content())
                .build();
    }
}
