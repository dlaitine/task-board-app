package fi.dlaitine.task.board.mapper;

import fi.dlaitine.task.board.dto.ChatMessageResponseDto;
import fi.dlaitine.task.board.dto.CreateChatMessageDto;
import fi.dlaitine.task.board.entity.ChatMessageEntity;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static fi.dlaitine.task.board.TestData.TEST_CHAT_MESSAGE_1_CONTENT;
import static fi.dlaitine.task.board.TestData.TEST_CHAT_MESSAGE_1_CREATED_AT;
import static fi.dlaitine.task.board.TestData.TEST_CHAT_MESSAGE_2_CONTENT;
import static fi.dlaitine.task.board.TestData.TEST_CHAT_MESSAGE_2_CREATED_AT;
import static fi.dlaitine.task.board.TestData.TEST_CHAT_USERNAME_1;
import static fi.dlaitine.task.board.TestData.TEST_CHAT_USERNAME_2;
import static fi.dlaitine.task.board.TestData.getTestChatMessage1;
import static fi.dlaitine.task.board.TestData.getTestChatMessage2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ChatMessageMapperTest {

    @Test
    @DisplayName("Test mapping CreateChatMessageDto to ChatMessageEntity, should succeed")
    void test_when_mappingCreateChatMessageDtoToChatMessageEntity_then_shouldSucceed() {
        CreateChatMessageDto createMessageDto = new CreateChatMessageDto(TEST_CHAT_USERNAME_1, TEST_CHAT_MESSAGE_1_CONTENT);

        ChatMessageEntity mappedMessage = ChatMessageMapper.toChatMessageEntity(createMessageDto);
        assertEquals(TEST_CHAT_USERNAME_1, mappedMessage.getUsername());
        assertEquals(TEST_CHAT_MESSAGE_1_CONTENT, mappedMessage.getContent());
        assertNull(mappedMessage.getId());
        assertNull(mappedMessage.getBoardId());
        assertNull(mappedMessage.getCreatedAt());
    }

    @Test
    @DisplayName("Test mapping ChatMessageEntity to ChatMessageResponseDto, should succeed")
    void test_when_mappingChatMessageEntityToChatMessageResponseDto_then_shouldSucceed() {
        Integer messageId = 1;
        ChatMessageEntity messageEntity = getTestChatMessage1(UUID.randomUUID());
        messageEntity.setId(messageId);

        ChatMessageResponseDto mappedMessage = ChatMessageMapper.toChatMessageResponseDto(messageEntity);
        assertEquals(messageId, mappedMessage.getId());
        assertEquals(TEST_CHAT_USERNAME_1, mappedMessage.getUsername());
        assertEquals(TEST_CHAT_MESSAGE_1_CONTENT, mappedMessage.getContent());
        assertEquals(TEST_CHAT_MESSAGE_1_CREATED_AT, mappedMessage.getCreatedAt());
    }

    @Test
    @DisplayName("Test mapping ChatMessageEntities to ChatMessageResponseDtos, should succeed")
    void test_when_mappingChatMessageEntitiesToChatMessageResponseDtos_then_shouldSucceed() {
        Integer message1Id = 100;
        ChatMessageEntity messageEntity1 = getTestChatMessage1(UUID.randomUUID());
        messageEntity1.setId(message1Id);

        Integer message2Id = 200;
        ChatMessageEntity messageEntity2 = getTestChatMessage2(UUID.randomUUID());
        messageEntity2.setId(message2Id);

        List<ChatMessageResponseDto> mappedMessages = ChatMessageMapper.toChatMessageResponseDtoList(
                Arrays.asList(messageEntity1, messageEntity2));
        assertEquals(2, mappedMessages.size());

        ChatMessageResponseDto mappedMessage1 = mappedMessages.get(0);
        assertEquals(message1Id, mappedMessage1.getId());
        assertEquals(TEST_CHAT_USERNAME_1, mappedMessage1.getUsername());
        assertEquals(TEST_CHAT_MESSAGE_1_CONTENT, mappedMessage1.getContent());
        assertEquals(TEST_CHAT_MESSAGE_1_CREATED_AT, mappedMessage1.getCreatedAt());

        ChatMessageResponseDto mappedMessage2 = mappedMessages.get(1);
        assertEquals(message2Id, mappedMessage2.getId());
        assertEquals(TEST_CHAT_USERNAME_2, mappedMessage2.getUsername());
        assertEquals(TEST_CHAT_MESSAGE_2_CONTENT, mappedMessage2.getContent());
        assertEquals(TEST_CHAT_MESSAGE_2_CREATED_AT, mappedMessage2.getCreatedAt());
    }

}
