package fi.dlaitine.task.board.service;

import fi.dlaitine.task.board.dto.ChatMessageResponseDto;
import fi.dlaitine.task.board.dto.CreateChatMessageDto;
import fi.dlaitine.task.board.entity.ChatMessageEntity;
import fi.dlaitine.task.board.mapper.ChatMessageMapper;
import fi.dlaitine.task.board.repository.ChatMessageRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);
    private final ChatMessageRepository repository;
    @Autowired
    public ChatService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public ChatMessageResponseDto addChatMessage(UUID boardId, CreateChatMessageDto message) {
        ChatMessageEntity messageEntity = ChatMessageMapper.toChatMessageEntity(message);
        messageEntity.setBoardId(boardId);
        messageEntity.setCreatedAt(ZonedDateTime.now().toInstant().toEpochMilli());

        try {
            messageEntity = repository.save(messageEntity);
        } catch (DataIntegrityViolationException e) {
            LOGGER.warn("Exception was thrown when saving new chat message {} to database: {}", message, e.getMessage(), e);
            throw e;
        }

        return ChatMessageMapper.toChatMessageResponseDto(messageEntity);
    }

}
