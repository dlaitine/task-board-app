package fi.dlaitine.task.service;

import fi.dlaitine.task.dto.CreateTaskDto;
import fi.dlaitine.task.dto.UpdateTaskDto;
import fi.dlaitine.task.dto.TaskResponseDto;
import fi.dlaitine.task.entity.TaskEntity;
import fi.dlaitine.task.exception.TaskNotFoundException;
import fi.dlaitine.task.mapper.TaskMapper;
import fi.dlaitine.task.repository.TaskRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public List<TaskResponseDto> getTasks() {
        List<TaskEntity> taskEntities = repository.findAll();
        return TaskMapper.fromTaskEntityList(taskEntities);
    }

    public TaskResponseDto addTask(CreateTaskDto task) {
        TaskEntity taskEntity = TaskMapper.fromCreateTask(task);

        // Add new tasks to backlog
        taskEntity.setStatus(TaskEntity.Status.BACKLOG);
        // Add new tasks to the end of status group
        taskEntity.setIndex(repository.findMaxIndexForStatusGroup(taskEntity.getStatus()) + 1);

        TaskEntity savedEntity;

        try {
            savedEntity = repository.save(taskEntity);
        } catch (DataIntegrityViolationException e) {
            LOGGER.warn("Exception was thrown when saving new task {} to database: {}", task, e.getMessage(), e);
            throw e;
        }

        return TaskMapper.fromTaskEntity(savedEntity);
    }

    @Transactional
    public List<TaskResponseDto> updateTask(int taskId, UpdateTaskDto updateTask) throws TaskNotFoundException {
        Optional<TaskEntity> optionalTask = repository.findById(taskId);

        if (optionalTask.isEmpty()) {
            throw new TaskNotFoundException("Task with id " + taskId + " not found.");
        }

        TaskEntity originalTask = optionalTask.get();

        TaskEntity.Status destinationStatus = TaskEntity.Status.valueOf(updateTask.getStatus().name());
        List<TaskEntity> movedTasks = handleIndexShifts(originalTask.getStatus(), destinationStatus, originalTask.getIndex(), updateTask.getIndex());

        originalTask.setTitle(updateTask.getTitle());
        originalTask.setDescription(updateTask.getDescription());
        originalTask.setStatus(TaskEntity.Status.valueOf(updateTask.getStatus().name()));
        originalTask.setIndex(updateTask.getIndex());

        movedTasks.add(originalTask);

        return TaskMapper.fromTaskEntityList(movedTasks);
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

            List<TaskEntity> tasks = repository.findByStatusEqualsAndIndexBetween(originStatus, startIndex, endIndex);
            tasks.forEach(task -> {
                if (task.getIndex() > originIndex && task.getIndex() <= destinationIndex) {
                    task.setIndex(task.getIndex() - 1);
                }
                else if (task.getIndex() >= destinationIndex && task.getIndex() < originIndex) {
                    task.setIndex(task.getIndex() + 1);
                }
            });
            return tasks;
        } else {
            List<TaskEntity> originTasks = repository.findByStatusEqualsAndIndexGreaterThan(originStatus, originIndex);
            originTasks.forEach(task -> task.setIndex(task.getIndex() - 1));
            List<TaskEntity> combinedTasks = new ArrayList<>(originTasks);

            List<TaskEntity> destinationTasks = repository.findByStatusEqualsAndIndexGreaterThanEqual(destinationStatus, destinationIndex);
            destinationTasks.forEach(task -> task.setIndex(task.getIndex() + 1));
            combinedTasks.addAll(destinationTasks);

            return combinedTasks;
        }
    }
}
