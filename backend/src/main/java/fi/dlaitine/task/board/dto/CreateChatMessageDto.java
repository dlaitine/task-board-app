package fi.dlaitine.task.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CreateChatMessageDto(@JsonProperty("username") @Size(min = 1, max = 50) String username, @JsonProperty("content") @NotEmpty String content) {}