package fi.dlaitine.task.board.controller;

import fi.dlaitine.task.board.dto.SocketErrorDto;
import fi.dlaitine.task.board.exception.BoardNotFoundException;
import fi.dlaitine.task.board.dto.CreateTaskDto;
import fi.dlaitine.task.board.dto.UpdateTaskDto;
import fi.dlaitine.task.board.dto.TaskResponseDto;
import fi.dlaitine.task.board.exception.TaskNotFoundException;
import fi.dlaitine.task.board.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Controller
public class TaskSocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSocketController.class);

    private final TaskService taskService;

    @Autowired
    public TaskSocketController(TaskService taskService) {
        this.taskService = taskService;
    }

    @MessageMapping("/{boardId}/new-task")
    @SendTo("/topic/{boardId}/new-task")
    public TaskResponseDto handleNewTask(@DestinationVariable UUID boardId,
                                         @Valid CreateTaskDto newTask) throws BoardNotFoundException {
        LOGGER.info("Received a request to create a new task to board '{}' with title '{}'", boardId, newTask.title());
        return taskService.addTask(boardId, newTask);
    }

    @MessageMapping("/{boardId}/update-task/{taskId}")
    @SendTo("/topic/{boardId}/update-tasks")
    public List<TaskResponseDto> handleUpdateTask(@DestinationVariable UUID boardId,
                                                  @DestinationVariable Integer taskId,
                                                  @Valid UpdateTaskDto updateTask) throws TaskNotFoundException {
        LOGGER.info("Received an update request for task '{}' on board '{}'", taskId, boardId);
        return taskService.updateTask(boardId, taskId, updateTask);
    }

    @MessageExceptionHandler
    @SendToUser("/queue/error")
    public SocketErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOGGER.warn("Received request with invalid arguments: {}", e.getMessage(), e);
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult != null) {
            return new SocketErrorDto("Invalid values for following fields: " + bindingResult.getFieldErrors()
                    .stream().map(FieldError::getField).toList());
        }
        return new SocketErrorDto("Invalid request");
    }

    @MessageExceptionHandler
    @SendToUser("/queue/error")
    public SocketErrorDto handleBoardNotFoundException(BoardNotFoundException e) {
        LOGGER.warn("Error occurred during task processing: {}", e.getMessage(), e);
        return new SocketErrorDto("Board not found");
    }

    @MessageExceptionHandler
    @SendToUser("/queue/error")
    public SocketErrorDto handleTaskNotFoundException(TaskNotFoundException e) {
        LOGGER.warn("Error occurred during task update: {}", e.getMessage(), e);
        return new SocketErrorDto("Task not found");
    }

    @MessageExceptionHandler
    @SendToUser("/queue/error")
    public SocketErrorDto handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        LOGGER.warn("Error occurred during task saving: {}", e.getMessage(), e);
        return new SocketErrorDto("Invalid data");
    }

    @MessageExceptionHandler
    @SendToUser("/queue/error")
    public SocketErrorDto handleException(Exception e) {
        LOGGER.warn("Error occurred during task processing: {}", e.getMessage(), e);
        return new SocketErrorDto("Internal error");
    }
}
