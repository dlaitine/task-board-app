package fi.dlaitine.task.board.mapper;

import fi.dlaitine.task.board.dto.CreateTaskDto;
import fi.dlaitine.task.board.dto.TaskResponseDto;
import fi.dlaitine.task.board.dto.TaskStatus;
import fi.dlaitine.task.board.entity.TaskEntity;
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
                .status(mapEnum(entity.getStatus(), TaskStatus.class))
                .position(entity.getPosition())
                .build();
    }

    public static TaskEntity toTaskEntity(CreateTaskDto dto) {
        return TaskEntity.builder()
                .title(dto.title())
                .description(dto.description())
                .build();

    }

    /**
     * Maps an enum constant from one enum type to another based on their common values.
     *
     * @param <E>
     *     Enum type of the value to be mapped
     * @param <T>
     *     Enum type to which the value will be mapped
     * @param enum1
     *     Enum constant from the source enum type to be mapped
     * @param enum2Class
     *     Class object representing the destination enum type
     * @return mapped enum constant from the destination enum type
     * @throws IllegalArgumentException if the specified enum constant is not found in the destination enum type
     */
    private static <E extends Enum<E>, T extends Enum<T>> T mapEnum(E enum1, Class<T> enum2Class) throws IllegalArgumentException {
        return Enum.valueOf(enum2Class, enum1.name());
    }


}
