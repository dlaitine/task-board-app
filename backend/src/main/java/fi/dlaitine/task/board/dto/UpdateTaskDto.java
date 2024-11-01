package fi.dlaitine.task.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateTaskDto {

    @JsonProperty("title")
    @NotEmpty
    private String title;

    @JsonProperty("description")
    @Nullable
    private String description;

    @JsonProperty("status")
    @NotNull
    private TaskStatus status;

    @JsonProperty("index")
    @NotNull
    private Integer index;

}
