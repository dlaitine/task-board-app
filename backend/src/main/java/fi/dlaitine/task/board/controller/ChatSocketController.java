package fi.dlaitine.task.board.controller;

import fi.dlaitine.task.board.dto.ChatMessageResponseDto;
import fi.dlaitine.task.board.dto.CreateChatMessageDto;
import fi.dlaitine.task.board.dto.SocketErrorDto;
import fi.dlaitine.task.board.exception.BoardNotFoundException;
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

    private final ChatService chatService;

    @Autowired
    public ChatSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/{boardId}/new-chat-message")
    @SendTo("/topic/{boardId}/new-chat-message")
    public ChatMessageResponseDto handleNewMessage(@DestinationVariable UUID boardId,
                                                   @Valid CreateChatMessageDto newMessage) throws BoardNotFoundException {
        return chatService.addChatMessage(boardId, newMessage);
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
        LOGGER.warn("Error occurred during chat message processing: {}", e.getMessage(), e);
        return new SocketErrorDto("Board not found");
    }

    @MessageExceptionHandler
    @SendToUser("/queue/error")
    public SocketErrorDto handleException(Exception e) {
        LOGGER.warn("Error occurred during chat message processing: {}", e.getMessage(), e);
        return new SocketErrorDto("Internal error");
    }
}
