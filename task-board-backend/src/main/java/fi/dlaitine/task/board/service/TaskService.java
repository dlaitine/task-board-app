package fi.dlaitine.task.board.service;

import fi.dlaitine.task.board.dto.TaskResponseDto;
import fi.dlaitine.task.board.exception.BoardNotFoundException;
import fi.dlaitine.task.board.exception.TaskNotFoundException;
import fi.dlaitine.task.board.dto.CreateTaskDto;
import fi.dlaitine.task.board.dto.UpdateTaskDto;
import fi.dlaitine.task.board.entity.TaskEntity;
import fi.dlaitine.task.board.mapper.TaskMapper;
import fi.dlaitine.task.board.repository.BoardRepository;
import fi.dlaitine.task.board.repository.TaskRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final BoardRepository boardRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(BoardRepository boardRepository,
                       TaskRepository taskRepository) {
        this.boardRepository = boardRepository;
        this.taskRepository = taskRepository;
    }

    public TaskResponseDto addTask(UUID boardId, CreateTaskDto task) throws BoardNotFoundException {
        if (!boardRepository.existsById(boardId)) {
            LOGGER.warn("Board '{}' not found", boardId);
            throw new BoardNotFoundException(String.format("Board '%s' not found.", boardId));
        }

        TaskEntity taskEntity = TaskMapper.toTaskEntity(task);
        taskEntity.setBoardId(boardId);
        // Add new tasks to backlog
        taskEntity.setStatus(TaskEntity.Status.BACKLOG);
        // Add new tasks to the end of status group
        taskEntity.setPosition(taskRepository.findMaxPositionForStatusGroup(taskEntity.getStatus()) + 1);

        try {
            taskEntity = taskRepository.save(taskEntity);
        } catch (DataAccessException e) {
            LOGGER.warn("Exception was thrown when saving new task {} to database: {}", task, e.getMessage(), e);
            throw e;
        }

        return TaskMapper.toTaskResponseDto(taskEntity);
    }

    @Transactional
    public List<TaskResponseDto> updateTask(UUID boardId, Integer taskId, UpdateTaskDto updateTask) throws TaskNotFoundException {
        Optional<TaskEntity> optionalTask = taskRepository.findByBoardIdAndId(boardId, taskId);

        if (optionalTask.isEmpty()) {
            LOGGER.warn("Task {} for board {} not found.", taskId, boardId);
            throw new TaskNotFoundException(String.format("Task '%s' for board '%s' not found.", taskId, boardId));
        }

        TaskEntity originalTask = optionalTask.get();

        TaskEntity.Status destinationStatus = TaskEntity.Status.valueOf(updateTask.getStatus().name());
        List<TaskEntity> movedTasks = handlePositionShifts(originalTask.getStatus(), destinationStatus, originalTask.getPosition(), updateTask.getPosition());

        originalTask.setTitle(updateTask.getTitle());
        originalTask.setDescription(updateTask.getDescription());
        originalTask.setStatus(TaskEntity.Status.valueOf(updateTask.getStatus().name()));
        originalTask.setPosition(updateTask.getPosition());

        movedTasks.add(originalTask);

        return TaskMapper.toTaskResponseDtoList(movedTasks);
    }

    /**
     * Updates positions for tasks affected by task update.
     *
     * <p>
     *     If status stays the same:
     *      - Tasks with same status having position greater than {@code originalPosition} but lower or equal {@code destinationPosition}
     *        will have their positions decreased by one
     *      - Tasks with same status having position greater or equal to {@code destinationPosition} but lower than {@code originalPosition}
     *        will have their positions increased by one
     *     If task status changes:
     *      - Tasks with origin status and position greater than {@code originalPosition} will have their positions subtracted by one
     *      - Tasks with destination status and position greater or equal to {@code destinationPosition} will have their positions increased by one
     * </p>
     * @param originStatus
     *      Original status for task
     * @param destinationStatus
     *      New status for task
     * @param originPosition
     *      Original position for task
     * @param destinationPosition
     *      Destination position for task
     * @return tasks which had their positions updated
     */
    private List<TaskEntity> handlePositionShifts(TaskEntity.Status originStatus, TaskEntity.Status destinationStatus,
                                                  int originPosition, int destinationPosition) {
        if (originStatus == destinationStatus && originPosition == destinationPosition) {
            return new ArrayList<>();
        }

        if (originStatus == destinationStatus) {
            int startPosition = originPosition < destinationPosition ? originPosition + 1 : destinationPosition;
            int endPosition = destinationPosition > originPosition ? destinationPosition : originPosition - 1;

            List<TaskEntity> tasks = taskRepository.findByStatusEqualsAndPositionBetween(originStatus, startPosition, endPosition);
            tasks.forEach(task -> {
                if (task.getPosition() > originPosition && task.getPosition() <= destinationPosition) {
                    task.setPosition(task.getPosition() - 1);
                }
                else if (task.getPosition() >= destinationPosition && task.getPosition() < originPosition) {
                    task.setPosition(task.getPosition() + 1);
                }
            });
            return tasks;
        } else {
            List<TaskEntity> originTasks = taskRepository.findByStatusEqualsAndPositionGreaterThan(originStatus, originPosition);
            originTasks.forEach(task -> task.setPosition(task.getPosition() - 1));
            List<TaskEntity> combinedTasks = new ArrayList<>(originTasks);

            List<TaskEntity> destinationTasks = taskRepository.findByStatusEqualsAndPositionGreaterThanEqual(destinationStatus, destinationPosition);
            destinationTasks.forEach(task -> task.setPosition(task.getPosition() + 1));
            combinedTasks.addAll(destinationTasks);

            return combinedTasks;
        }
    }
}
