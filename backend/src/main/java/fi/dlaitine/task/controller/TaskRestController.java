package fi.dlaitine.task.controller;

import fi.dlaitine.task.dto.TaskResponseDto;
import fi.dlaitine.task.service.TaskService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskRestController {

    private final TaskService service;

    @Autowired
    public TaskRestController(TaskService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<List<TaskResponseDto>> getTasks() {
        return ResponseEntity.ok(service.getTasks());
    }


}
