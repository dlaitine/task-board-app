package fi.dlaitine.task.board.controller;

import fi.dlaitine.task.board.dto.BoardResponseDto;
import fi.dlaitine.task.board.dto.ChatMessageResponseDto;
import fi.dlaitine.task.board.dto.CreateBoardDto;
import fi.dlaitine.task.board.dto.TaskResponseDto;
import fi.dlaitine.task.board.entity.BoardEntity;
import fi.dlaitine.task.board.entity.ChatMessageEntity;
import fi.dlaitine.task.board.entity.TaskEntity;
import fi.dlaitine.task.board.repository.BoardRepository;
import fi.dlaitine.task.board.repository.ChatMessageRepository;
import fi.dlaitine.task.board.repository.TaskRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import static fi.dlaitine.task.board.TestData.TEST_CHAT_MESSAGE_1_CONTENT;
import static fi.dlaitine.task.board.TestData.TEST_CHAT_MESSAGE_1_CREATED_AT;
import static fi.dlaitine.task.board.TestData.TEST_CHAT_USERNAME_1;
import static fi.dlaitine.task.board.TestData.TEST_CHAT_MESSAGE_2_CONTENT;
import static fi.dlaitine.task.board.TestData.TEST_CHAT_MESSAGE_2_CREATED_AT;
import static fi.dlaitine.task.board.TestData.TEST_CHAT_USERNAME_2;
import static fi.dlaitine.task.board.TestData.TEST_PRIVATE_BOARD_1_IS_PRIVATE;
import static fi.dlaitine.task.board.TestData.TEST_PRIVATE_BOARD_1_NAME;
import static fi.dlaitine.task.board.TestData.TEST_PUBLIC_BOARD_1_IS_PRIVATE;
import static fi.dlaitine.task.board.TestData.TEST_PUBLIC_BOARD_1_NAME;
import static fi.dlaitine.task.board.TestData.TEST_PUBLIC_BOARD_2_IS_PRIVATE;
import static fi.dlaitine.task.board.TestData.TEST_PUBLIC_BOARD_2_NAME;
import static fi.dlaitine.task.board.TestData.TEST_TASK_1_DESCRIPTION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_1_POSITION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_1_STATUS;
import static fi.dlaitine.task.board.TestData.TEST_TASK_1_TITLE;
import static fi.dlaitine.task.board.TestData.TEST_TASK_2_DESCRIPTION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_2_POSITION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_2_STATUS;
import static fi.dlaitine.task.board.TestData.TEST_TASK_2_TITLE;
import static fi.dlaitine.task.board.TestData.getTestChatMessage1;
import static fi.dlaitine.task.board.TestData.getTestChatMessage2;
import static fi.dlaitine.task.board.TestData.getTestPrivateBoard1;
import static fi.dlaitine.task.board.TestData.getTestPublicBoard1;
import static fi.dlaitine.task.board.TestData.getTestPublicBoard2;
import static fi.dlaitine.task.board.TestData.getTestTask1;
import static fi.dlaitine.task.board.TestData.getTestTask2;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BoardRestControllerIT {

    private UUID testPublicBoard1Id;

    @LocalServerPort
    int port;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        initBoardTestData();
    }

    @AfterEach
    void teardown() {
        chatMessageRepository.deleteAll();
        taskRepository.deleteAll();
        boardRepository.deleteAll();
        assertEquals(0, boardRepository.count());
    }

    @Test
    @DisplayName("Test fetching public boards, should return ok")
    void test_whenGetPublicBoards_shouldReturnOk() {
        Response response = given()
                .when()
                .get("/api/v1/boards")
                .then()
                .statusCode(200)
                .extract().response();

        List<BoardResponseDto> boardResponseDtos = Arrays.asList(response.getBody().as(BoardResponseDto[].class));
        assertEquals(2, boardResponseDtos.size());

        BoardResponseDto board1 = boardResponseDtos.get(0);

        assertAll("Board 1 assertions",
                () -> assertEquals(TEST_PUBLIC_BOARD_1_NAME, board1.getName()),
                () -> assertEquals(TEST_PUBLIC_BOARD_1_IS_PRIVATE, board1.getIsPrivate()),
                () -> assertNull(board1.getTasks()),
                () -> assertNull(board1.getChatMessages()));

        BoardResponseDto board2 = boardResponseDtos.get(1);
        assertAll("Board 2 assertions",
                () -> assertEquals(TEST_PUBLIC_BOARD_2_NAME, board2.getName()),
                () -> assertEquals(TEST_PUBLIC_BOARD_2_IS_PRIVATE, board2.getIsPrivate()),
                () -> assertNull(board2.getTasks()),
                () -> assertNull(board2.getChatMessages()));
    }

    @Test
    @DisplayName("Test fetching public board without tasks and chat messages, should return ok")
    void test_whenGetPublicBoard_withoutTasksAndMessages_shouldReturnOk() {
        BoardResponseDto boardResponseDto = internalGetBoard(testPublicBoard1Id, false, false);

        assertAll("Board assertions",
                () -> assertEquals(TEST_PUBLIC_BOARD_1_NAME, boardResponseDto.getName()),
                () -> assertEquals(TEST_PUBLIC_BOARD_1_IS_PRIVATE, boardResponseDto.getIsPrivate()),
                () -> assertNull(boardResponseDto.getTasks()),
                () -> assertNull(boardResponseDto.getChatMessages()));
    }

    @Test
    @DisplayName("Test fetching public board with tasks and chat messages, should return ok")
    void test_whenGetPublicBoard_withTasksAndMessages_shouldReturnOk() {
        BoardResponseDto boardResponseDto = internalGetBoard(testPublicBoard1Id, true, true);

        assertAll("Board assertions",
                () -> assertEquals(TEST_PUBLIC_BOARD_1_NAME, boardResponseDto.getName()),
                () -> assertEquals(TEST_PUBLIC_BOARD_1_IS_PRIVATE, boardResponseDto.getIsPrivate()),
                () -> assertNotNull(boardResponseDto.getTasks()),
                () -> assertNotNull(boardResponseDto.getChatMessages()));

        assertEquals(2, boardResponseDto.getTasks().size());

        TaskResponseDto task1 = boardResponseDto.getTasks().get(0);
        assertTask(TEST_TASK_1_TITLE, TEST_TASK_1_DESCRIPTION, TEST_TASK_1_STATUS.name(),
                TEST_TASK_1_POSITION, task1);

        TaskResponseDto task2 = boardResponseDto.getTasks().get(1);
        assertTask(TEST_TASK_2_TITLE, TEST_TASK_2_DESCRIPTION, TEST_TASK_2_STATUS.name(),
                TEST_TASK_2_POSITION, task2);

        assertEquals(2, boardResponseDto.getChatMessages().size());

        ChatMessageResponseDto chatMessage1 = boardResponseDto.getChatMessages().get(0);
        assertChatMessage(TEST_CHAT_USERNAME_1, TEST_CHAT_MESSAGE_1_CONTENT,
                TEST_CHAT_MESSAGE_1_CREATED_AT, chatMessage1);

        ChatMessageResponseDto chatMessage2 = boardResponseDto.getChatMessages().get(1);
        assertChatMessage(TEST_CHAT_USERNAME_2, TEST_CHAT_MESSAGE_2_CONTENT,
                TEST_CHAT_MESSAGE_2_CREATED_AT, chatMessage2);
    }

    @Test
    @DisplayName("Test fetching public board with tasks and without chat messages, should return ok")
    void test_whenGetPublicBoard_withTasksAndWithoutMessages_shouldReturnOk() {
        BoardResponseDto boardResponseDto = internalGetBoard(testPublicBoard1Id, true, false);

        assertAll("Board assertions",
                () -> assertEquals(TEST_PUBLIC_BOARD_1_NAME, boardResponseDto.getName()),
                () -> assertEquals(TEST_PUBLIC_BOARD_1_IS_PRIVATE, boardResponseDto.getIsPrivate()),
                () -> assertNotNull(boardResponseDto.getTasks()),
                () -> assertEquals(2, boardResponseDto.getTasks().size()),
                () -> assertNull(boardResponseDto.getChatMessages()));
    }

    @Test
    @DisplayName("Test fetching public board without tasks and with chat messages, should return ok")
    void test_whenGetPublicBoard_withoutTasksAndWithMessages_shouldReturnOk() {
        BoardResponseDto boardResponseDto = internalGetBoard(testPublicBoard1Id, false, true);

        assertAll("Board assertions",
                () -> assertEquals(TEST_PUBLIC_BOARD_1_NAME, boardResponseDto.getName()),
                () -> assertEquals(TEST_PUBLIC_BOARD_1_IS_PRIVATE, boardResponseDto.getIsPrivate()),
                () -> assertNull(boardResponseDto.getTasks()),
                () -> assertNotNull(boardResponseDto.getChatMessages()),
                () -> assertEquals(2, boardResponseDto.getChatMessages().size()));
    }

    @Test
    @DisplayName("Test fetching public board when board doesn't exist, should return not found")
    void test_whenGetPublicBoard_andBoardDoesntExist_shouldReturnNotFound() {
        given()
                .when()
                .get("/api/v1/boards/{boardId}", UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Test fetching public board when board id is in invalid format, should return bad request")
    void test_whenGetPublicBoard_andUUIDIsInvalid_shouldReturnBadRequest() {
        given()
                .when()
                .get("/api/v1/boards/not-an-uuid")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Test creating new public board, should return ok")
    void test_whenCreatePublicBoard_shouldReturnOk() {
        CreateBoardDto newPublicBoard = new CreateBoardDto("New Public Board", false);
        internalCreateNewBoard(newPublicBoard, HttpStatus.CREATED);
    }

    @Test
    @DisplayName("Test creating new private board, should return ok")
    void test_whenCreatePrivateBoard_shouldReturnOk() {
        CreateBoardDto newPrivateBoard = new CreateBoardDto("New Private Board", true);
        internalCreateNewBoard(newPrivateBoard, HttpStatus.CREATED);
    }

    @Test
    @DisplayName("Test creating new public board with empty name, should return bad request")
    void test_whenCreatePublicBoard_withEmptyName_shouldReturnBadRequest() {
        CreateBoardDto invalidBoard = new CreateBoardDto("", false);
        internalCreateNewBoard(invalidBoard, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Test creating new public board with too long name, should return bad request")
    void test_whenCreatePublicBoard_withTooLongName_shouldReturnBadRequest() {
        String tooLongBoardName = StringUtils.repeat("toolongname", 25);
        CreateBoardDto invalidBoard = new CreateBoardDto(tooLongBoardName, false);
        internalCreateNewBoard(invalidBoard, HttpStatus.BAD_REQUEST);
    }


    private void assertTask(String expectedTitle, String expectedDescription, String expectedStatus,
                           Integer expectedPosition, TaskResponseDto actualTask) {
        assertAll("Task 2 assertions",
                () -> assertNotNull(actualTask.getId()),
                () -> assertEquals(expectedTitle, actualTask.getTitle()),
                () -> assertEquals(expectedDescription, actualTask.getDescription()),
                () -> assertEquals(expectedStatus, actualTask.getStatus().name()),
                () -> assertEquals(expectedPosition, actualTask.getPosition()));
    }

    private void assertChatMessage(String expectedUsername, String expectedContent,
                                  Long expectedCreatedAt, ChatMessageResponseDto actualChatMessage) {
        assertAll("Chat message assertions",
                () -> assertEquals(expectedUsername, actualChatMessage.getUsername()),
                () -> assertEquals(expectedContent, actualChatMessage.getContent()),
                () -> assertEquals(expectedCreatedAt, actualChatMessage.getCreatedAt()));
    }

    private BoardResponseDto internalGetBoard(UUID boardId, boolean includeTasks, boolean includeChatMessages) {
        BoardResponseDto boardResponseDto = given()
                .queryParam("includeTasks", includeTasks)
                .queryParam("includeChatMessages", includeChatMessages)
                .when()
                .get("/api/v1/boards/{boardId}", boardId)
                .then()
                .statusCode(200)
                .extract().response()
                .getBody().as(BoardResponseDto.class);

        assertNotNull(boardResponseDto);
        return boardResponseDto;
    }

    private void internalCreateNewBoard(CreateBoardDto newBoard, HttpStatus expectedStatus) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(newBoard)
                .when()
                .post("/api/v1/boards")
                .then()
                .statusCode(expectedStatus.value())
                .extract().response();

        if (expectedStatus == HttpStatus.OK) {
            BoardResponseDto createdBoard = response.getBody().as(BoardResponseDto.class);

            // Assert board response
            assertNotNull(createdBoard);
            assertNotNull(createdBoard.getId());
            assertEquals(newBoard.name(), createdBoard.getName());
            assertEquals(newBoard.isPrivate(), createdBoard.getIsPrivate());
            assertNull(createdBoard.getTasks());
            assertNull(createdBoard.getChatMessages());

            // Assert board entity
            assertEquals(4, boardRepository.count());
            Optional<BoardEntity> optionalCreatedBoardEntity = boardRepository.findById(createdBoard.getId());
            assertTrue(optionalCreatedBoardEntity.isPresent());
            BoardEntity createdBoardEntity = optionalCreatedBoardEntity.get();
            assertEquals(newBoard.name(), createdBoardEntity.getName());
            assertEquals(newBoard.isPrivate(), createdBoardEntity.isPrivate());
        }
    }

    private void initBoardTestData() {
        // Board 1 test data initialization

        BoardEntity publicBoard1 = getTestPublicBoard1();
        publicBoard1 = boardRepository.save(publicBoard1);
        testPublicBoard1Id = publicBoard1.getId();

        TaskEntity board1Task1 = getTestTask1(testPublicBoard1Id);
        TaskEntity board1Task2 = getTestTask2(testPublicBoard1Id);
        taskRepository.saveAll(List.of(board1Task1, board1Task2));

        ChatMessageEntity board1Chat1 = getTestChatMessage1(testPublicBoard1Id);
        ChatMessageEntity board1Chat2 = getTestChatMessage2(testPublicBoard1Id);
        chatMessageRepository.saveAll(List.of(board1Chat1, board1Chat2));

        // Board 2 test data initialization

        BoardEntity publicBoard2 = getTestPublicBoard2();
        publicBoard2 = boardRepository.save(publicBoard2);
        UUID TEST_PUBLIC_BOARD_2_ID = publicBoard2.getId();

        TaskEntity board2Task1 = getTestTask1(TEST_PUBLIC_BOARD_2_ID);
        taskRepository.save(board2Task1);

        // Board 3 test data initialization

        BoardEntity privateBoard1 = getTestPrivateBoard1();
        privateBoard1 = boardRepository.save(privateBoard1);
        UUID TEST_PRIVATE_BOARD_1_ID = privateBoard1.getId();

        TaskEntity board3Task1 = getTestTask2(TEST_PRIVATE_BOARD_1_ID);
        taskRepository.save(board3Task1);

        ChatMessageEntity board3Chat1 = getTestChatMessage2(TEST_PRIVATE_BOARD_1_ID);
        chatMessageRepository.save(board3Chat1);

        assertEquals(3, boardRepository.count());
    }

}
