package fi.dlaitine.task.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateTaskDto {

    @NotEmpty
    private String title;
    @Nullable
    private String description;
    @NotNull
    private TaskStatus status;
    @NotNull
    private Integer index;

}
