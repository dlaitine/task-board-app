package fi.dlaitine.task.board.controller;

import fi.dlaitine.task.board.dto.CreateTaskDto;
import fi.dlaitine.task.board.dto.SocketErrorDto;
import fi.dlaitine.task.board.dto.TaskResponseDto;
import fi.dlaitine.task.board.dto.TaskStatus;
import fi.dlaitine.task.board.dto.UpdateTaskDto;
import fi.dlaitine.task.board.entity.TaskEntity;
import fi.dlaitine.task.board.util.EnumHelper;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static fi.dlaitine.task.board.TestData.TEST_TASK_1_DESCRIPTION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_1_POSITION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_1_STATUS;
import static fi.dlaitine.task.board.TestData.TEST_TASK_1_TITLE;
import static fi.dlaitine.task.board.TestData.TEST_TASK_2_DESCRIPTION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_2_POSITION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_2_STATUS;
import static fi.dlaitine.task.board.TestData.TEST_TASK_2_TITLE;
import static fi.dlaitine.task.board.TestData.TEST_TASK_3_DESCRIPTION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_3_POSITION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_3_STATUS;
import static fi.dlaitine.task.board.TestData.TEST_TASK_3_TITLE;
import static fi.dlaitine.task.board.TestData.TEST_TASK_5_DESCRIPTION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_5_TITLE;
import static fi.dlaitine.task.board.TestData.TEST_TASK_6_DESCRIPTION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_6_TITLE;
import static fi.dlaitine.task.board.TestData.getTestTask1;
import static fi.dlaitine.task.board.TestData.getTestTask2;
import static fi.dlaitine.task.board.TestData.getTestTask3;
import static fi.dlaitine.task.board.TestData.getTestTask4;
import static fi.dlaitine.task.board.TestData.getTestTask5;
import static fi.dlaitine.task.board.TestData.getTestTask6;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskSocketControllerIT extends SocketControllerITBase {

    // TASK SOCKET PATHS
    private static final String NEW_TASK_CREATE_PATH = WEBSOCKET_APP + "/%s/new-task";
    private static final String NEW_TASK_LISTEN_PATH = WEBSOCKET_TOPIC + "/%s/new-task";
    private static final String TASK_UPDATE_CREATE_PATH = WEBSOCKET_APP + "/%s/update-task/%s";
    private static final String TASK_UPDATE_LISTEN_PATH = WEBSOCKET_TOPIC + "/%s/update-tasks";

    // CREATE NEW TASK TESTS

    @Test
    @DisplayName("Test creating new task, should be sent to listening clients")
    void test_when_createNewTask_then_clientsShouldReceiveNewTask() throws Exception {
        initializeTestBoard();

        TestStompFrameHandler<TaskResponseDto> stompHandler = new TestStompFrameHandler<>(TaskResponseDto.class);
        stompSession.subscribe(getNewTaskListenPath(testBoardId), stompHandler);

        stompSession.send(getNewTaskCreatePath(testBoardId), getCreateTask1Dto());

        TaskResponseDto createdTask = stompHandler.poll(2);
        assertTaskResponse(TEST_TASK_1_TITLE, TEST_TASK_1_DESCRIPTION,
                EnumHelper.mapEnum(TEST_TASK_1_STATUS, TaskStatus.class), TEST_TASK_1_POSITION, createdTask);
        assertEquals(1, taskRepository.count());
    }

    @Test
    @DisplayName("Test creating new task without description, should be sent to listening clients")
    void test_when_createNewTaskWithoutDescription_then_clientsShouldReceiveNewTask() throws Exception {
        initializeTestBoard();

        TestStompFrameHandler<TaskResponseDto> stompHandler = new TestStompFrameHandler<>(TaskResponseDto.class);
        stompSession.subscribe(getNewTaskListenPath(testBoardId), stompHandler);

        CreateTaskDto taskDto = new CreateTaskDto(TEST_TASK_1_TITLE, "");
        stompSession.send(getNewTaskCreatePath(testBoardId), taskDto);

        TaskResponseDto createdTask = stompHandler.poll(2);
        assertTaskResponse(TEST_TASK_1_TITLE, "",
                EnumHelper.mapEnum(TEST_TASK_1_STATUS, TaskStatus.class), TEST_TASK_1_POSITION, createdTask);
        assertEquals(1, taskRepository.count());
    }

    @Test
    @DisplayName("Test creating new task when target board doesn't exist, should return an error")
    void test_when_createNewTask_when_targetBoardNotExists_then_clientShouldReceiveError() throws Exception {
        TestStompFrameHandler<SocketErrorDto> stompHandler = new TestStompFrameHandler<>(SocketErrorDto.class);
        stompSession.subscribe(ERROR_LISTEN_PATH, stompHandler);

        stompSession.send(getNewTaskCreatePath(UUID.randomUUID()), getCreateTask1Dto());

        assertEquals("Board not found", stompHandler.poll(2).errorMsg());
        assertEquals(0, taskRepository.count());
    }

    @Test
    @DisplayName("Test creating new task with empty title, should return an error")
    void test_when_createNewTask_when_titleIsEmpty_then_clientShouldReceiveError() throws Exception {
        TestStompFrameHandler<SocketErrorDto> stompHandler = new TestStompFrameHandler<>(SocketErrorDto.class);
        stompSession.subscribe(ERROR_LISTEN_PATH, stompHandler);

        CreateTaskDto invalidTaskDto = new CreateTaskDto("", TEST_TASK_1_DESCRIPTION);
        stompSession.send(getNewTaskCreatePath(UUID.randomUUID()), invalidTaskDto);

        assertEquals("Invalid values for following fields: [title]", stompHandler.poll(2).errorMsg());
        assertEquals(0, taskRepository.count());
    }

    @Test
    @DisplayName("Test creating new task with null title, should return an error")
    void test_when_createNewTask_when_titleIsNull_then_clientShouldReceiveError() throws Exception {
        TestStompFrameHandler<SocketErrorDto> stompHandler = new TestStompFrameHandler<>(SocketErrorDto.class);
        stompSession.subscribe(ERROR_LISTEN_PATH, stompHandler);

        CreateTaskDto invalidTaskDto = new CreateTaskDto(null, TEST_TASK_1_DESCRIPTION);
        stompSession.send(getNewTaskCreatePath(UUID.randomUUID()), invalidTaskDto);

        assertEquals("Invalid values for following fields: [title]", stompHandler.poll(2).errorMsg());
        assertEquals(0, taskRepository.count());
    }

    // UPDATE TASK TESTS

    @Test
    @DisplayName("Test updating a task position with same status, changed tasks should be sent to listening clients")
    void test_when_updateTaskPosition_then_clientsShouldReceiveUpdatedTasks() throws Exception {
        initializeTestBoard();
        taskRepository.save(getTestTask1(testBoardId));
        taskRepository.save(getTestTask2(testBoardId));
        taskRepository.save(getTestTask3(testBoardId));
        taskRepository.save(getTestTask4(testBoardId));
        taskRepository.save(getTestTask5(testBoardId));
        taskRepository.save(getTestTask6(testBoardId));

        List<TaskEntity> tasks = taskRepository.findAll();
        TaskEntity task2 = tasks.stream().filter(t -> t.getTitle().equals(TEST_TASK_2_TITLE)).findFirst().get();

        UpdateTaskDto taskUpdate = UpdateTaskDto.builder()
                .title(task2.getTitle())
                .description(task2.getDescription())
                .status(TaskStatus.BACKLOG)
                .position(2)
                .build();

        TestStompFrameHandler<TaskResponseDto[]> stompHandler = new TestStompFrameHandler<>(TaskResponseDto[].class);
        stompSession.subscribe(getUpdateTaskListenPath(testBoardId), stompHandler);

        stompSession.send(getUpdateTaskCreatePath(testBoardId, task2.getId()), taskUpdate);

        TaskResponseDto[] updatedTasks = stompHandler.poll(5);
        assertEquals(2, updatedTasks.length);

        assertTaskResponse(TEST_TASK_3_TITLE, TEST_TASK_3_DESCRIPTION, TaskStatus.BACKLOG,
                1, updatedTasks[0]);
        assertTaskResponse(TEST_TASK_2_TITLE, TEST_TASK_2_DESCRIPTION, TaskStatus.BACKLOG,
                2, updatedTasks[1]);

        assertEquals(6, taskRepository.count());
    }

    @Test
    @DisplayName("Test updating a task status and position, changed tasks should be sent to listening clients")
    void test_when_updateTaskPositionAndStatus_then_clientsShouldReceiveUpdatedTasks() throws Exception {
        initializeTestBoard();
        taskRepository.save(getTestTask1(testBoardId));
        taskRepository.save(getTestTask2(testBoardId));
        taskRepository.save(getTestTask3(testBoardId));
        taskRepository.save(getTestTask4(testBoardId));
        taskRepository.save(getTestTask5(testBoardId));
        taskRepository.save(getTestTask6(testBoardId));

        List<TaskEntity> tasks = taskRepository.findAll();
        TaskEntity task2 = tasks.stream().filter(t -> t.getTitle().equals(TEST_TASK_2_TITLE)).findFirst().get();

        UpdateTaskDto taskUpdate = UpdateTaskDto.builder()
                .title(task2.getTitle())
                .description(task2.getDescription())
                .status(TaskStatus.IN_PROGRESS)
                .position(1)
                .build();

        TestStompFrameHandler<TaskResponseDto[]> stompHandler = new TestStompFrameHandler<>(TaskResponseDto[].class);
        stompSession.subscribe(getUpdateTaskListenPath(testBoardId), stompHandler);

        stompSession.send(getUpdateTaskCreatePath(testBoardId, task2.getId()), taskUpdate);

        TaskResponseDto[] updatedTasks = stompHandler.poll(5);
        assertEquals(4, updatedTasks.length);

        assertTaskResponse(TEST_TASK_3_TITLE, TEST_TASK_3_DESCRIPTION, TaskStatus.BACKLOG,
                1, updatedTasks[0]);
        assertTaskResponse(TEST_TASK_5_TITLE, TEST_TASK_5_DESCRIPTION, TaskStatus.IN_PROGRESS,
                2, updatedTasks[1]);
        assertTaskResponse(TEST_TASK_6_TITLE, TEST_TASK_6_DESCRIPTION, TaskStatus.IN_PROGRESS,
                3, updatedTasks[2]);
        assertTaskResponse(TEST_TASK_2_TITLE, TEST_TASK_2_DESCRIPTION, TaskStatus.IN_PROGRESS,
                1, updatedTasks[3]);

        assertEquals(6, taskRepository.count());
    }

    @Test
    @DisplayName("Test updating a task with missing values, should return an error")
    void test_when_updateTaskWithMissingValues_then_clientShouldReceiveError() throws Exception {
        TestStompFrameHandler<SocketErrorDto> stompHandler = new TestStompFrameHandler<>(SocketErrorDto.class);
        stompSession.subscribe(ERROR_LISTEN_PATH, stompHandler);


        UpdateTaskDto invalidUpdate = UpdateTaskDto.builder().build();
        stompSession.send(getUpdateTaskCreatePath(UUID.randomUUID(), 0), invalidUpdate);

        String errorMsg = stompHandler.poll(5).errorMsg();

        String pattern = "^Invalid values for following fields:\\s*\\[[^]]*(?:(?:status|title|position)[^]]*){3}]$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(errorMsg);
        assertTrue(matcher.matches());
    }

    @Test
    @DisplayName("Test updating a task with invalid board ID, should return an error")
    void test_when_updateTaskWithInvalidBoardId_then_clientShouldReceiveError() throws Exception {
        initializeTestBoard();
        initializeTestTasks(testBoardId);

        List<TaskEntity> tasks = taskRepository.findAll();
        TaskEntity task2 = tasks.stream().filter(t -> t.getTitle().equals(TEST_TASK_2_TITLE)).findFirst().get();

        UpdateTaskDto taskUpdate = UpdateTaskDto.builder()
                .title(task2.getTitle())
                .description(task2.getDescription())
                .status(TaskStatus.TODO)
                .position(0)
                .build();

        TestStompFrameHandler<SocketErrorDto> stompHandler = new TestStompFrameHandler<>(SocketErrorDto.class);
        stompSession.subscribe(ERROR_LISTEN_PATH, stompHandler);

        stompSession.send(getUpdateTaskCreatePath(UUID.randomUUID(), task2.getId()), taskUpdate);

        String errorMsg = stompHandler.poll(5).errorMsg();
        assertEquals("Task not found", errorMsg);
    }

    @Test
    @DisplayName("Test updating a task with invalid task ID, should return an error")
    void test_when_updateTaskWithInvalidTaskId_then_clientShouldReceiveError() throws Exception {
        initializeTestBoard();
        initializeTestTasks(testBoardId);

        List<TaskEntity> tasks = taskRepository.findAll();
        TaskEntity task2 = tasks.stream().filter(t -> t.getTitle().equals(TEST_TASK_2_TITLE)).findFirst().get();

        UpdateTaskDto taskUpdate = UpdateTaskDto.builder()
                .title(task2.getTitle())
                .description(task2.getDescription())
                .status(TaskStatus.TODO)
                .position(0)
                .build();

        TestStompFrameHandler<SocketErrorDto> stompHandler = new TestStompFrameHandler<>(SocketErrorDto.class);
        stompSession.subscribe(ERROR_LISTEN_PATH, stompHandler);

        stompSession.send(getUpdateTaskCreatePath(testBoardId, Integer.MAX_VALUE), taskUpdate);

        String errorMsg = stompHandler.poll(5).errorMsg();
        assertEquals("Task not found", errorMsg);
    }


    protected void initializeTestTasks(UUID boardId) {
        taskRepository.save(getTestTask1(boardId));
        taskRepository.save(getTestTask2(boardId));
        taskRepository.save(getTestTask3(boardId));
    }


    protected String getNewTaskCreatePath(UUID boardId) {
        return String.format(NEW_TASK_CREATE_PATH, boardId);
    }

    protected String getNewTaskListenPath(UUID boardId) {
        return String.format(NEW_TASK_LISTEN_PATH, boardId);
    }

    protected String getUpdateTaskCreatePath(UUID boardId, Integer taskId) {
        return String.format(TASK_UPDATE_CREATE_PATH, boardId, taskId);
    }

    protected String getUpdateTaskListenPath(UUID boardId) {
        return String.format(TASK_UPDATE_LISTEN_PATH, boardId);
    }

    protected CreateTaskDto getCreateTask1Dto() {
        return new CreateTaskDto(TEST_TASK_1_TITLE, TEST_TASK_1_DESCRIPTION);
    }

    protected void assertTaskResponse(String expectedTitle, String expectedDescription,
                                      TaskStatus expectedStatus, Integer expectedPosition,
                                      TaskResponseDto actualTask) {
        assertAll("Task response assertions",
                () -> assertNotNull(actualTask.getId()),
                () -> assertEquals(expectedTitle, actualTask.getTitle()),
                () -> assertEquals(expectedDescription, actualTask.getDescription()),
                () -> assertEquals(expectedStatus, actualTask.getStatus()),
                () -> assertEquals(expectedPosition, actualTask.getPosition()));
    }
}
