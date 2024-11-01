package fi.dlaitine.task.board.controller;

import fi.dlaitine.task.board.dto.ChatMessageResponseDto;
import fi.dlaitine.task.board.dto.CreateChatMessageDto;
import fi.dlaitine.task.board.service.ChatService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ChatSocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatSocketController.class);

    private final ChatService service;

    @Autowired
    public ChatSocketController(ChatService service) {
        this.service = service;
    }

    @MessageMapping("/{boardId}/new-chat-message")
    @SendTo("/topic/{boardId}/new-chat-message")
    public ChatMessageResponseDto handleNewMessage(@DestinationVariable UUID boardId,
                                                   @Valid CreateChatMessageDto newMessage) {
        return service.addChatMessage(boardId, newMessage);
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
