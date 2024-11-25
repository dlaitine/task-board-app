package fi.dlaitine.task.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBoardDto(@JsonProperty("name") @NotEmpty @Size(min = 1, max = 255) String name, @JsonProperty("is_private") @NotNull Boolean isPrivate) {}