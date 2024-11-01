package fi.dlaitine.task.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;

public record CreateTaskDto(@JsonProperty("title") @NotEmpty String title, @JsonProperty("description") @Nullable String description) {}
