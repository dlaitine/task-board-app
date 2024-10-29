package fi.dlaitine.task.controller;

import fi.dlaitine.task.dto.ChatMessageResponseDto;
import fi.dlaitine.task.service.ChatMessageService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chats")
public class ChatRestController {

    private final ChatMessageService service;

    @Autowired
    public ChatRestController(ChatMessageService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<List<ChatMessageResponseDto>> getChatMessages() {
        return ResponseEntity.ok(service.getChatMessages());
    }


}
