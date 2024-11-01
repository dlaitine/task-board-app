package fi.dlaitine.task.board.mapper;

import fi.dlaitine.task.board.dto.BoardResponseDto;
import fi.dlaitine.task.board.dto.CreateBoardDto;
import fi.dlaitine.task.board.entity.BoardEntity;
import java.util.List;

public class BoardMapper {

    private BoardMapper() {

    }

    public static List<BoardResponseDto> toBoardResponseDtoList(List<BoardEntity> entities) {
        return entities.stream()
                .map(BoardMapper::toBoardResponseDto)
                .toList();
    }

    public static BoardResponseDto toBoardResponseDto(BoardEntity entity) {
        return BoardResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public static BoardEntity toBoardEntity(CreateBoardDto dto) {
        return BoardEntity.builder()
                .name(dto.name())
                .isPrivate(dto.isPrivate())
                .build();
    }
}
