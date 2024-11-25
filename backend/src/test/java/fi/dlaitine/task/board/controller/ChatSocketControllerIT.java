package fi.dlaitine.task.board.controller;

import fi.dlaitine.task.board.dto.ChatMessageResponseDto;
import fi.dlaitine.task.board.dto.CreateChatMessageDto;
import fi.dlaitine.task.board.dto.SocketErrorDto;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatSocketControllerIT extends SocketControllerITBase {

    // CHAT SOCKET PATHS

    private static final String NEW_CHAT_MESSAGE_CREATE_PATH = WEBSOCKET_APP + "/%s/new-chat-message";
    private static final String NEW_CHAT_MESSAGE_LISTEN_PATH = WEBSOCKET_TOPIC + "/%s/new-chat-message";

    // TEST DATA

    private static final String TEST_USER_NAME = "Test User";

    private static final String TEST_MESSAGE_1_CONTENT = "Hello";

    // NEW CHAT MESSAGE TESTS

    @Test
    @DisplayName("Test sending chat message, should be sent to listening clients")
    void test_whenCreateChatMessage_then_clientsShouldReceiveMessage() throws Exception {
        long start = ZonedDateTime.now().toInstant().toEpochMilli();
        initializeTestBoard();

        TestStompFrameHandler<ChatMessageResponseDto> stompHandler = new TestStompFrameHandler<>(ChatMessageResponseDto.class);
        stompSession.subscribe(getNewChatMessageListenPath(testBoardId), stompHandler);

        stompSession.send(getNewChatMessageCreatePath(testBoardId), getCreateChatMessage1Dto());

        ChatMessageResponseDto chatMessage = stompHandler.poll(2);

        long end = ZonedDateTime.now().toInstant().toEpochMilli();

        assertChatMessage(TEST_USER_NAME, TEST_MESSAGE_1_CONTENT, chatMessage);
        assertNotNull(chatMessage.getCreatedAt());
        assertTrue(isEpochBetween(start, end, chatMessage.getCreatedAt()));

        assertEquals(1, chatMessageRepository.count());
    }

    @Test
    @DisplayName("Test sending chat message with invalid board ID, should return an error")
    void test_whenCreateChatMessage_with_invalidBoardId_then_clientsShouldReceiveMessage() throws Exception {
        initializeTestBoard();

        TestStompFrameHandler<SocketErrorDto> stompHandler = new TestStompFrameHandler<>(SocketErrorDto.class);
        stompSession.subscribe(ERROR_LISTEN_PATH, stompHandler);

        stompSession.send(getNewChatMessageCreatePath(UUID.randomUUID()), getCreateChatMessage1Dto());

        String errorMessage = stompHandler.poll(2).errorMsg();

        assertEquals("Board not found", errorMessage);
        assertEquals(0, chatMessageRepository.count());
    }

    @Test
    @DisplayName("Test sending chat message with empty username, should return an error")
    void test_whenCreateChatMessage_with_emptyUsername_then_clientsShouldReceiveMessage() throws Exception {
        TestStompFrameHandler<SocketErrorDto> stompHandler = new TestStompFrameHandler<>(SocketErrorDto.class);
        stompSession.subscribe(ERROR_LISTEN_PATH, stompHandler);

        CreateChatMessageDto chatMessage = new CreateChatMessageDto("", TEST_MESSAGE_1_CONTENT);
        stompSession.send(getNewChatMessageCreatePath(UUID.randomUUID()), chatMessage);

        String errorMessage = stompHandler.poll(2).errorMsg();

        assertEquals("Invalid values for following fields: [username]", errorMessage);
        assertEquals(0, chatMessageRepository.count());
    }

    @Test
    @DisplayName("Test sending chat message with too long username, should return an error")
    void test_whenCreateChatMessage_with_tooLongUsername_then_clientsShouldReceiveMessage() throws Exception {
        TestStompFrameHandler<SocketErrorDto> stompHandler = new TestStompFrameHandler<>(SocketErrorDto.class);
        stompSession.subscribe(ERROR_LISTEN_PATH, stompHandler);

        String tooLongUsername = StringUtils.repeat("a", 51);
        CreateChatMessageDto chatMessage = new CreateChatMessageDto(tooLongUsername, TEST_MESSAGE_1_CONTENT);
        stompSession.send(getNewChatMessageCreatePath(UUID.randomUUID()), chatMessage);

        String errorMessage = stompHandler.poll(2).errorMsg();

        assertEquals("Invalid values for following fields: [username]", errorMessage);
        assertEquals(0, chatMessageRepository.count());
    }

    @Test
    @DisplayName("Test sending chat message with empty content, should return an error")
    void test_whenCreateChatMessage_with_emptyContent_then_clientsShouldReceiveMessage() throws Exception {
        TestStompFrameHandler<SocketErrorDto> stompHandler = new TestStompFrameHandler<>(SocketErrorDto.class);
        stompSession.subscribe(ERROR_LISTEN_PATH, stompHandler);

        CreateChatMessageDto chatMessage = new CreateChatMessageDto(TEST_USER_NAME, "");
        stompSession.send(getNewChatMessageCreatePath(UUID.randomUUID()), chatMessage);

        String errorMessage = stompHandler.poll(2).errorMsg();

        assertEquals("Invalid values for following fields: [content]", errorMessage);
        assertEquals(0, chatMessageRepository.count());
    }

    private CreateChatMessageDto getCreateChatMessage1Dto() {
        return new CreateChatMessageDto(TEST_USER_NAME, TEST_MESSAGE_1_CONTENT);
    }

    private void assertChatMessage(String expectedUsername, String expectedContent,
                                   ChatMessageResponseDto actualChatMessage) {
        assertAll("Chat message assertions",
                () -> assertNotNull(actualChatMessage.getId()),
                () -> assertEquals(expectedUsername, actualChatMessage.getUsername()),
                () -> assertEquals(expectedContent, actualChatMessage.getContent()));
    }

    private boolean isEpochBetween(long start, long end, long actual) {
        return actual >= start && actual <= end;
    }

    private String getNewChatMessageCreatePath(UUID boardId) {
        return String.format(NEW_CHAT_MESSAGE_CREATE_PATH, boardId);
    }

    private String getNewChatMessageListenPath(UUID boardId) {
        return String.format(NEW_CHAT_MESSAGE_LISTEN_PATH, boardId);
    }


}
