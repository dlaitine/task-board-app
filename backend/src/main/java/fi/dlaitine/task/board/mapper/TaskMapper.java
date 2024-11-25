package fi.dlaitine.task.board.mapper;

import fi.dlaitine.task.board.dto.CreateTaskDto;
import fi.dlaitine.task.board.dto.TaskResponseDto;
import fi.dlaitine.task.board.dto.TaskStatus;
import fi.dlaitine.task.board.entity.TaskEntity;
import fi.dlaitine.task.board.util.EnumHelper;
import java.util.List;

public class TaskMapper {

    private TaskMapper() {

    }

    public static List<TaskResponseDto> toTaskResponseDtoList(List<TaskEntity> entities) {
        return entities.stream()
                .map(TaskMapper::toTaskResponseDto)
                .toList();
    }

    public static TaskResponseDto toTaskResponseDto(TaskEntity entity) {
        return TaskResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(EnumHelper.mapEnum(entity.getStatus(), TaskStatus.class))
                .position(entity.getPosition())
                .build();
    }

    public static TaskEntity toTaskEntity(CreateTaskDto dto) {
        return TaskEntity.builder()
                .title(dto.title())
                .description(dto.description())
                .build();

    }

}
