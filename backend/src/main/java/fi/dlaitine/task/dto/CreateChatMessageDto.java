package fi.dlaitine.task.dto;

import jakarta.validation.constraints.NotEmpty;

public record CreateChatMessageDto(@NotEmpty String userName, @NotEmpty String content) {}