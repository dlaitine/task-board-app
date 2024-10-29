package fi.dlaitine.task.controller;

import fi.dlaitine.task.dto.ChatMessageResponseDto;
import fi.dlaitine.task.dto.CreateChatMessageDto;
import fi.dlaitine.task.service.ChatMessageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Controller
public class ChatSocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatSocketController.class);

    private final ChatMessageService service;

    @Autowired
    public ChatSocketController(ChatMessageService service) {
        this.service = service;
    }

    @MessageMapping("/chat-message")
    @SendTo("/topic/chat-message")
    public ChatMessageResponseDto handleNewMessage(@Valid CreateChatMessageDto newMessage) {
        return service.addChatMessage(newMessage);
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
    public String handleException(Exception e) {
        LOGGER.warn("Error occurred during chat message processing: {}", e.getMessage(), e);
        return "Internal error";
    }
}
