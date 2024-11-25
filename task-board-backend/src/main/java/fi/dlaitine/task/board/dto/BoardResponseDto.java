package fi.dlaitine.task.board.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardResponseDto {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("is_private")
    private Boolean isPrivate;

    @JsonProperty("tasks")
    private List<TaskResponseDto> tasks;

    @JsonProperty("chat_messages")
    private List<ChatMessageResponseDto> chatMessages;
}
