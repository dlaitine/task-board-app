package fi.dlaitine.task.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatMessageResponseDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("content")
    private String content;

    // Epoch millis
    @JsonProperty("created_at")
    private Long createdAt;
}
