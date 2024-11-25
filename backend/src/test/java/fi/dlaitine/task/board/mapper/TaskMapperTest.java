package fi.dlaitine.task.board.mapper;

import fi.dlaitine.task.board.dto.CreateTaskDto;
import fi.dlaitine.task.board.dto.TaskResponseDto;
import fi.dlaitine.task.board.entity.TaskEntity;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static fi.dlaitine.task.board.TestData.TEST_TASK_1_DESCRIPTION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_1_POSITION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_1_STATUS;
import static fi.dlaitine.task.board.TestData.TEST_TASK_1_TITLE;
import static fi.dlaitine.task.board.TestData.TEST_TASK_2_DESCRIPTION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_2_POSITION;
import static fi.dlaitine.task.board.TestData.TEST_TASK_2_STATUS;
import static fi.dlaitine.task.board.TestData.TEST_TASK_2_TITLE;
import static fi.dlaitine.task.board.TestData.getTestTask1;
import static fi.dlaitine.task.board.TestData.getTestTask2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class TaskMapperTest {

    @Test
    @DisplayName("Test mapping CreateTaskDto to TaskEntity should succeed")
    void test_when_mappingCreateTaskDtoToTaskEntity_then_shouldSucceed() {
        CreateTaskDto createTaskDto = new CreateTaskDto(TEST_TASK_1_TITLE, TEST_TASK_1_DESCRIPTION);

        TaskEntity mappedTask = TaskMapper.toTaskEntity(createTaskDto);
        assertEquals(TEST_TASK_1_TITLE, mappedTask.getTitle());
        assertEquals(TEST_TASK_1_DESCRIPTION, mappedTask.getDescription());
        assertNull(mappedTask.getBoardId());
        assertNull(mappedTask.getId());
        assertNull(mappedTask.getStatus());
        assertNull(mappedTask.getPosition());
    }

    @Test
    @DisplayName("Test mapping TaskEntity to TaskResponseDto, should succeed")
    void test_when_mappingTaskEntityToTaskResponseDto_then_shouldSucceed() {
        Integer taskId = 1;
        TaskEntity taskEntity = getTestTask1(UUID.randomUUID());
        taskEntity.setId(taskId);

        TaskResponseDto mappedTask = TaskMapper.toTaskResponseDto(taskEntity);
        assertEquals(taskId, mappedTask.getId());
        assertEquals(TEST_TASK_1_TITLE, mappedTask.getTitle());
        assertEquals(TEST_TASK_1_DESCRIPTION, mappedTask.getDescription());
        assertEquals(TEST_TASK_1_STATUS, TaskEntity.Status.valueOf(mappedTask.getStatus().name()));
        assertEquals(TEST_TASK_1_POSITION, mappedTask.getPosition());
    }

    @Test
    @DisplayName("Test mapping TaskEntities to TaskResponseDtos, should succeed")
    void test_when_mappingTaskEntitiesToTaskResponseDtos_then_shouldSucceed() {
        Integer task1Id = 100;
        TaskEntity taskEntity1 = getTestTask1(UUID.randomUUID());
        taskEntity1.setId(task1Id);

        Integer task2Id = 200;
        TaskEntity taskEntity2 = getTestTask2(UUID.randomUUID());
        taskEntity2.setId(task2Id);

        List<TaskResponseDto> mappedTasks = TaskMapper.toTaskResponseDtoList(Arrays.asList(taskEntity1, taskEntity2));
        assertEquals(2, mappedTasks.size());

        TaskResponseDto mappedTask1 = mappedTasks.get(0);
        assertEquals(task1Id, mappedTask1.getId());
        assertEquals(TEST_TASK_1_TITLE, mappedTask1.getTitle());
        assertEquals(TEST_TASK_1_DESCRIPTION, mappedTask1.getDescription());
        assertEquals(TEST_TASK_1_STATUS, TaskEntity.Status.valueOf(mappedTask1.getStatus().name()));
        assertEquals(TEST_TASK_1_POSITION, mappedTask1.getPosition());

        TaskResponseDto mappedTask2 = mappedTasks.get(1);
        assertEquals(task2Id, mappedTask2.getId());
        assertEquals(TEST_TASK_2_TITLE, mappedTask2.getTitle());
        assertEquals(TEST_TASK_2_DESCRIPTION, mappedTask2.getDescription());
        assertEquals(TEST_TASK_2_STATUS, TaskEntity.Status.valueOf(mappedTask2.getStatus().name()));
        assertEquals(TEST_TASK_2_POSITION, mappedTask2.getPosition());
    }


}
