package fi.dlaitine.task.board;

import fi.dlaitine.task.board.entity.BoardEntity;
import fi.dlaitine.task.board.entity.ChatMessageEntity;
import fi.dlaitine.task.board.entity.TaskEntity;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class TestData {

    // BOARD TEST DATA

    public static final String TEST_PUBLIC_BOARD_1_NAME = "Public Board 1";
    public static final Boolean TEST_PUBLIC_BOARD_1_IS_PRIVATE = false;

    public static final String TEST_PUBLIC_BOARD_2_NAME = "Public Board 2";
    public static final Boolean TEST_PUBLIC_BOARD_2_IS_PRIVATE = false;

    public static final String TEST_PRIVATE_BOARD_1_NAME = "Private Board 1";
    public static final Boolean TEST_PRIVATE_BOARD_1_IS_PRIVATE = true;

    // TASK TEST DATA

    public static final String TEST_TASK_1_TITLE = "Test Task Title 1";
    public static final String TEST_TASK_1_DESCRIPTION = "Test Task Description 1";
    public static final TaskEntity.Status TEST_TASK_1_STATUS = TaskEntity.Status.BACKLOG;
    public static final Integer TEST_TASK_1_POSITION = 0;

    public static final String TEST_TASK_2_TITLE = "Test Task Title 2";
    public static final String TEST_TASK_2_DESCRIPTION = "Test Task Description 2";
    public static final TaskEntity.Status TEST_TASK_2_STATUS = TaskEntity.Status.BACKLOG;
    public static final Integer TEST_TASK_2_POSITION = 1;

    public static final String TEST_TASK_3_TITLE = "Task 3 Title";
    public static final String TEST_TASK_3_DESCRIPTION = "Test 3 Description";
    public static final TaskEntity.Status TEST_TASK_3_STATUS = TaskEntity.Status.BACKLOG;
    public static final Integer TEST_TASK_3_POSITION = 2;

    public static final String TEST_TASK_4_TITLE = "Test Task Title 4";
    public static final String TEST_TASK_4_DESCRIPTION = "Test Task Description 4";
    public static final TaskEntity.Status TEST_TASK_4_STATUS = TaskEntity.Status.IN_PROGRESS;
    public static final Integer TEST_TASK_4_POSITION = 0;

    public static final String TEST_TASK_5_TITLE = "Test Task Title 5";
    public static final String TEST_TASK_5_DESCRIPTION = "Test Task Description 5";
    public static final TaskEntity.Status TEST_TASK_5_STATUS = TaskEntity.Status.IN_PROGRESS;
    public static final Integer TEST_TASK_5_POSITION = 1;

    public static final String TEST_TASK_6_TITLE = "Task 6 Title";
    public static final String TEST_TASK_6_DESCRIPTION = "Test 6 Description";
    public static final TaskEntity.Status TEST_TASK_6_STATUS = TaskEntity.Status.IN_PROGRESS;
    public static final Integer TEST_TASK_6_POSITION = 2;


    // CHAT MESSAGE TEST DATA

    public static final String TEST_CHAT_USERNAME_1 = "Test User 1";
    public static final String TEST_CHAT_MESSAGE_1_CONTENT = "Testing 1";
    public static final Long TEST_CHAT_MESSAGE_1_CREATED_AT = Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli();

    public static final String TEST_CHAT_USERNAME_2 = "Test User 2";
    public static final String TEST_CHAT_MESSAGE_2_CONTENT = "Testing 2";
    public static final Long TEST_CHAT_MESSAGE_2_CREATED_AT = Instant.now().toEpochMilli();

    private TestData() {}

    public static BoardEntity getTestPublicBoard1() {
        return BoardEntity.builder()
                .name(TEST_PUBLIC_BOARD_1_NAME)
                .isPrivate(TEST_PUBLIC_BOARD_1_IS_PRIVATE)
                .build();
    }

    public static BoardEntity getTestPublicBoard2() {
        return BoardEntity.builder()
                .name(TEST_PUBLIC_BOARD_2_NAME)
                .isPrivate(TEST_PUBLIC_BOARD_2_IS_PRIVATE)
                .build();
    }

    public static BoardEntity getTestPrivateBoard1() {
        return BoardEntity.builder()
                .name(TEST_PRIVATE_BOARD_1_NAME)
                .isPrivate(TEST_PRIVATE_BOARD_1_IS_PRIVATE)
                .build();
    }

    public static TaskEntity getTestTask1(UUID boardId) {
        return TaskEntity.builder()
                .boardId(boardId)
                .title(TEST_TASK_1_TITLE)
                .description(TEST_TASK_1_DESCRIPTION)
                .status(TEST_TASK_1_STATUS)
                .position(TEST_TASK_1_POSITION)
                .build();
    }

    public static TaskEntity getTestTask2(UUID boardId) {
        return TaskEntity.builder()
                .boardId(boardId)
                .title(TEST_TASK_2_TITLE)
                .description(TEST_TASK_2_DESCRIPTION)
                .status(TEST_TASK_2_STATUS)
                .position(TEST_TASK_2_POSITION)
                .build();
    }

    public static TaskEntity getTestTask3(UUID boardId) {
        return TaskEntity.builder()
                .boardId(boardId)
                .title(TEST_TASK_3_TITLE)
                .description(TEST_TASK_3_DESCRIPTION)
                .status(TEST_TASK_3_STATUS)
                .position(TEST_TASK_3_POSITION)
                .build();
    }

    public static TaskEntity getTestTask4(UUID boardId) {
        return TaskEntity.builder()
                .boardId(boardId)
                .title(TEST_TASK_4_TITLE)
                .description(TEST_TASK_4_DESCRIPTION)
                .status(TEST_TASK_4_STATUS)
                .position(TEST_TASK_4_POSITION)
                .build();
    }

    public static TaskEntity getTestTask5(UUID boardId) {
        return TaskEntity.builder()
                .boardId(boardId)
                .title(TEST_TASK_5_TITLE)
                .description(TEST_TASK_5_DESCRIPTION)
                .status(TEST_TASK_5_STATUS)
                .position(TEST_TASK_5_POSITION)
                .build();
    }

    public static TaskEntity getTestTask6(UUID boardId) {
        return TaskEntity.builder()
                .boardId(boardId)
                .title(TEST_TASK_6_TITLE)
                .description(TEST_TASK_6_DESCRIPTION)
                .status(TEST_TASK_6_STATUS)
                .position(TEST_TASK_6_POSITION)
                .build();
    }
    
    public static ChatMessageEntity getTestChatMessage1(UUID boardId) {
        return ChatMessageEntity.builder()
                .boardId(boardId)
                .username(TEST_CHAT_USERNAME_1)
                .content(TEST_CHAT_MESSAGE_1_CONTENT)
                .createdAt(TEST_CHAT_MESSAGE_1_CREATED_AT)
                .build();
    }

    public static ChatMessageEntity getTestChatMessage2(UUID boardId) {
        return ChatMessageEntity.builder()
                .boardId(boardId)
                .username(TEST_CHAT_USERNAME_2)
                .content(TEST_CHAT_MESSAGE_2_CONTENT)
                .createdAt(TEST_CHAT_MESSAGE_2_CREATED_AT)
                .build();
    }

}
