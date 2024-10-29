package fi.dlaitine.task.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TaskResponseDto {

    private Integer id;

    private String title;

    private String description;

    private TaskStatus status;

    private Integer index;

}
