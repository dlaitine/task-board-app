package fi.dlaitine.task.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;

public record CreateTaskDto(@NotEmpty String title, @Nullable String description) {}
