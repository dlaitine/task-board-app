package fi.dlaitine.task.board.util;

import fi.dlaitine.task.board.dto.TaskStatus;
import fi.dlaitine.task.board.entity.TaskEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EnumHelperTest {

    @Test
    @DisplayName("When mapping TaskStatus to TaskEntity.Status, should succeed")
    void test_when_mappingTaskStatusToTaskEntityStatus_then_shouldSucceed() {
        TaskEntity.Status mappedStatus = EnumHelper.mapEnum(TaskStatus.IN_PROGRESS, TaskEntity.Status.class);
        assertEquals(TaskEntity.Status.IN_PROGRESS, mappedStatus);
    }

    @Test
    @DisplayName("When mapping TaskEntity.Status to TaskStatus, should succeed")
    void test_when_mappingTaskEntityStatusToTaskStatus_then_shouldSucceed() {
        TaskStatus mappedStatus = EnumHelper.mapEnum(TaskEntity.Status.DONE, TaskStatus.class);
        assertEquals(TaskStatus.DONE, mappedStatus);
    }
}
