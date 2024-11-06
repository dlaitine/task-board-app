package fi.dlaitine.task.board.service;

import fi.dlaitine.task.board.dto.TaskResponseDto;
import fi.dlaitine.task.board.exception.TaskNotFoundException;
import fi.dlaitine.task.board.dto.CreateTaskDto;
import fi.dlaitine.task.board.dto.UpdateTaskDto;
import fi.dlaitine.task.board.entity.TaskEntity;
import fi.dlaitine.task.board.mapper.TaskMapper;
import fi.dlaitine.task.board.repository.TaskRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository repository;

    @Autowired
    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponseDto addTask(UUID boardId, CreateTaskDto task) {
        TaskEntity taskEntity = TaskMapper.toTaskEntity(task);
        taskEntity.setBoardId(boardId);
        // Add new tasks to backlog
        taskEntity.setStatus(TaskEntity.Status.BACKLOG);
        // Add new tasks to the end of status group
        taskEntity.setPosition(repository.findMaxIndexForStatusGroup(taskEntity.getStatus()) + 1);

        try {
            taskEntity = repository.save(taskEntity);
        } catch (DataIntegrityViolationException e) {
            LOGGER.warn("Exception was thrown when saving new task {} to database: {}", task, e.getMessage(), e);
            throw e;
        }

        return TaskMapper.toTaskResponseDto(taskEntity);
    }

    @Transactional
    public List<TaskResponseDto> updateTask(UUID boardId, Integer taskId, UpdateTaskDto updateTask) throws TaskNotFoundException {
        Optional<TaskEntity> optionalTask = repository.findByBoardIdAndId(boardId, taskId);

        if (optionalTask.isEmpty()) {
            throw new TaskNotFoundException("Task for board " + boardId + " with id " + taskId + " not found.");
        }

        TaskEntity originalTask = optionalTask.get();

        TaskEntity.Status destinationStatus = TaskEntity.Status.valueOf(updateTask.getStatus().name());
        List<TaskEntity> movedTasks = handleIndexShifts(originalTask.getStatus(), destinationStatus, originalTask.getPosition(), updateTask.getPosition());

        originalTask.setTitle(updateTask.getTitle());
        originalTask.setDescription(updateTask.getDescription());
        originalTask.setStatus(TaskEntity.Status.valueOf(updateTask.getStatus().name()));
        originalTask.setPosition(updateTask.getPosition());

        movedTasks.add(originalTask);

        return TaskMapper.toTaskResponseDtoList(movedTasks);
    }

    /**
     * Updates indexes for tasks affected by task update.
     *
     * <p>
     *     If status stays the same:
     *      - Tasks with same status having index greater than {@code originalIndex} but lower or equal {@code destinationIndex}
     *        will have their indexes decreased by one
     *      - Tasks with same status having index greater or equal to {@code destinationIndex} but lower than {@code originalIndex}
     *        will have their indexes increased by one
     *     If task status changes:
     *      - Tasks with origin status and index greater than {@code originalIndex} will have their index subtracted by one
     *      - Tasks with destination status and index greater or equal to {@code destinationIndex} index will have their indexes increased by one
     * </p>
     * @param originStatus
     *      Original status for task
     * @param destinationStatus
     *      New status for task
     * @param originIndex
     *      Original index for task
     * @param destinationIndex
     *      Destination index for task
     * @return tasks which had their indexes updated
     */
    private List<TaskEntity> handleIndexShifts(TaskEntity.Status originStatus, TaskEntity.Status destinationStatus,
                                               int originIndex, int destinationIndex) {
        if (originStatus == destinationStatus && originIndex == destinationIndex) {
            return new ArrayList<>();
        }

        if (originStatus == destinationStatus) {
            int startIndex = originIndex < destinationIndex ? originIndex + 1 : destinationIndex;
            int endIndex = destinationIndex > originIndex ? destinationIndex : originIndex - 1;

            List<TaskEntity> tasks = repository.findByStatusEqualsAndPositionBetween(originStatus, startIndex, endIndex);
            tasks.forEach(task -> {
                if (task.getPosition() > originIndex && task.getPosition() <= destinationIndex) {
                    task.setPosition(task.getPosition() - 1);
                }
                else if (task.getPosition() >= destinationIndex && task.getPosition() < originIndex) {
                    task.setPosition(task.getPosition() + 1);
                }
            });
            return tasks;
        } else {
            List<TaskEntity> originTasks = repository.findByStatusEqualsAndPositionGreaterThan(originStatus, originIndex);
            originTasks.forEach(task -> task.setPosition(task.getPosition() - 1));
            List<TaskEntity> combinedTasks = new ArrayList<>(originTasks);

            List<TaskEntity> destinationTasks = repository.findByStatusEqualsAndPositionGreaterThanEqual(destinationStatus, destinationIndex);
            destinationTasks.forEach(task -> task.setPosition(task.getPosition() + 1));
            combinedTasks.addAll(destinationTasks);

            return combinedTasks;
        }
    }
}
