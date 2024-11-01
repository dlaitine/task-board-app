package fi.dlaitine.task.board.controller;

import fi.dlaitine.task.board.dto.CreateTaskDto;
import fi.dlaitine.task.board.dto.UpdateTaskDto;
import fi.dlaitine.task.board.dto.TaskResponseDto;
import fi.dlaitine.task.board.exception.BoardNotFoundException;
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

    private final TaskService service;

    @Autowired
    public TaskSocketController(TaskService service) {
        this.service = service;
    }

    @MessageMapping("/{boardId}/new-task")
    @SendTo("/topic/{boardId}/new-task")
    public TaskResponseDto handleNewTask(@DestinationVariable UUID boardId,
                                         @Valid CreateTaskDto newTask) {
        return service.addTask(boardId, newTask);
    }

    @MessageMapping("/{boardId}/update-task/{taskId}")
    @SendTo("/topic/{boardId}/update-tasks")
    public List<TaskResponseDto> handleUpdateTask(@DestinationVariable UUID boardId,
                                                  @DestinationVariable Integer taskId,
                                                  @Valid UpdateTaskDto updateTask) throws TaskNotFoundException {
        return service.updateTask(boardId, taskId, updateTask);
    }

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOGGER.warn("Received request with invalid arguments: {}", e.getMessage(), e);
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult != null) {
            return "Invalid values for following fields: " + bindingResult.getFieldErrors()
                    .stream().map(FieldError::getField).toList();
        }
        return "Invalid request";
    }

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public String handleBoardNotFoundException(BoardNotFoundException e) {
        LOGGER.warn("Error occurred during task update: {}", e.getMessage(), e);
        return "Invalid board id";
    }

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public String handleTaskNotFoundException(TaskNotFoundException e) {
        LOGGER.warn("Error occurred during task update: {}", e.getMessage(), e);
        return "Invalid task id";
    }

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public String handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        LOGGER.warn("Error occurred during task saving: {}", e.getMessage(), e);
        return "Invalid data";
    }

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public String handleException(Exception e) {
        LOGGER.warn("Error occurred during task processing: {}", e.getMessage(), e);
        return "Internal error";
    }
}
