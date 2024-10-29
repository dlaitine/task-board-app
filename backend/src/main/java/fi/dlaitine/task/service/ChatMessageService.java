package fi.dlaitine.task.service;

import fi.dlaitine.task.dto.ChatMessageResponseDto;
import fi.dlaitine.task.dto.CreateChatMessageDto;
import fi.dlaitine.task.entity.ChatMessageEntity;
import fi.dlaitine.task.mapper.ChatMessageMapper;
import fi.dlaitine.task.repository.ChatMessageRepository;
import java.time.ZonedDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageService.class);
    private final ChatMessageRepository repository;
    @Autowired
    public ChatMessageService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public List<ChatMessageResponseDto> getChatMessages() {
        List<ChatMessageEntity> messageEntities = repository.findAll();
        return ChatMessageMapper.fromChatMessageEntityList(messageEntities);
    }

    public ChatMessageResponseDto addChatMessage(CreateChatMessageDto message) {
        ChatMessageEntity messageEntity = ChatMessageMapper.fromCreateChatMessage(message);
        messageEntity.setCreatedAt(ZonedDateTime.now().toInstant().toEpochMilli());

        ChatMessageEntity savedEntity;

        try {
            savedEntity = repository.save(messageEntity);
        } catch (DataIntegrityViolationException e) {
            LOGGER.warn("Exception was thrown when saving new chat message {} to database: {}", message, e.getMessage(), e);
            throw e;
        }

        return ChatMessageMapper.fromChatMessageEntity(savedEntity);
    }

}
