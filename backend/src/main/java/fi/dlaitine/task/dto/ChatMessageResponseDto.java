package fi.dlaitine.task.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatMessageResponseDto {

    Integer id;
    String userName;
    String content;

    // Epoch millis
    long createdAt;
}
