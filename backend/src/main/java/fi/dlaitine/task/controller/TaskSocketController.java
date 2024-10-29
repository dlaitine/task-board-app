package fi.dlaitine.task.controller;

import fi.dlaitine.task.dto.CreateTaskDto;
import fi.dlaitine.task.dto.UpdateTaskDto;
import fi.dlaitine.task.dto.TaskResponseDto;
import fi.dlaitine.task.exception.TaskNotFoundException;
import fi.dlaitine.task.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
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

    @MessageMapping("/new-task")
    @SendTo("/topic/new-task")
    public TaskResponseDto handleNewTask(@Valid CreateTaskDto newTask) {
        return service.addTask(newTask);
    }

    @MessageMapping("/update-task/{taskId}")
    @SendTo("/topic/update-tasks")
    public List<TaskResponseDto> handleUpdateTask(@DestinationVariable int taskId, @Valid UpdateTaskDto updateTask) throws TaskNotFoundException {
        return service.updateTask(taskId, updateTask);
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
    public String handleTaskNotFoundException(TaskNotFoundException e) {
        LOGGER.warn("Error occurred during task update: {}", e.getMessage(), e);
        return "Invalid task id";
    }

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public String handleTaskNotFoundException(DataIntegrityViolationException e) {
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
