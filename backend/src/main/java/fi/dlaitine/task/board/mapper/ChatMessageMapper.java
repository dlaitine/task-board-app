package fi.dlaitine.task.board.mapper;

import fi.dlaitine.task.board.dto.ChatMessageResponseDto;
import fi.dlaitine.task.board.dto.CreateChatMessageDto;
import fi.dlaitine.task.board.entity.ChatMessageEntity;
import java.util.List;

public class ChatMessageMapper {

    private ChatMessageMapper() {

    }

    public static List<ChatMessageResponseDto> toChatMessageResponseDtoList(List<ChatMessageEntity> entities) {
        return entities.stream()
                .map(ChatMessageMapper::toChatMessageResponseDto)
                .toList();
    }

    public static ChatMessageResponseDto toChatMessageResponseDto(ChatMessageEntity entity) {
        return ChatMessageResponseDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static ChatMessageEntity toChatMessageEntity(CreateChatMessageDto dto) {
        return ChatMessageEntity.builder()
                .username(dto.username())
                .content(dto.content())
                .build();
    }
}
