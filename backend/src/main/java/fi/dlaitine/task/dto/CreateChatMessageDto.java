package fi.dlaitine.task.dto;

import jakarta.validation.constraints.NotEmpty;

public record CreateChatMessageDto(@NotEmpty String username, @NotEmpty String content) {}