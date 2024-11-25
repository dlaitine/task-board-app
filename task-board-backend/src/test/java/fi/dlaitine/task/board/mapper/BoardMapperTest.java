package fi.dlaitine.task.board.mapper;

import fi.dlaitine.task.board.dto.BoardResponseDto;
import fi.dlaitine.task.board.dto.CreateBoardDto;
import fi.dlaitine.task.board.entity.BoardEntity;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static fi.dlaitine.task.board.TestData.TEST_PRIVATE_BOARD_1_IS_PRIVATE;
import static fi.dlaitine.task.board.TestData.TEST_PRIVATE_BOARD_1_NAME;
import static fi.dlaitine.task.board.TestData.TEST_PUBLIC_BOARD_1_IS_PRIVATE;
import static fi.dlaitine.task.board.TestData.TEST_PUBLIC_BOARD_1_NAME;
import static fi.dlaitine.task.board.TestData.getTestPrivateBoard1;
import static fi.dlaitine.task.board.TestData.getTestPublicBoard1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class BoardMapperTest {

    @Test
    @DisplayName("Test mapping CreateBoardDto to BoardEntity, should succeed")
    void test_when_mappingCreateBoardDtoToBoardEntity_then_shouldSucceed() {
        CreateBoardDto createBoardDto = new CreateBoardDto(TEST_PUBLIC_BOARD_1_NAME, TEST_PRIVATE_BOARD_1_IS_PRIVATE);

        BoardEntity mappedBoard = BoardMapper.toBoardEntity(createBoardDto);

        assertEquals(TEST_PUBLIC_BOARD_1_NAME, mappedBoard.getName());
        assertEquals(TEST_PRIVATE_BOARD_1_IS_PRIVATE, mappedBoard.isPrivate());
        assertNull(mappedBoard.getId());
        assertNull(mappedBoard.getTasks());
        assertNull(mappedBoard.getChatMessages());
    }

    @Test
    @DisplayName("Test mapping BoardEntity to BoardResponseDto, should succeed")
    void test_when_mappingBoardEntityToBoardResponseDto_then_shouldSucceed() {
        UUID boardId = UUID.randomUUID();
        BoardEntity boardEntity = getTestPublicBoard1();
        boardEntity.setId(boardId);

        BoardResponseDto mappedBoard = BoardMapper.toBoardResponseDto(boardEntity);
        assertEquals(boardId, mappedBoard.getId());
        assertEquals(TEST_PUBLIC_BOARD_1_NAME, mappedBoard.getName());
        assertEquals(TEST_PUBLIC_BOARD_1_IS_PRIVATE, mappedBoard.getIsPrivate());
        assertNull(mappedBoard.getTasks());
        assertNull(mappedBoard.getChatMessages());
    }

    @Test
    @DisplayName("Test mapping BoardEntities to BoardResponseDtos, should succeed")
    void test_when_mappingBoardEntitiesToBoardResponseDtos_then_shouldSucceed() {
        UUID board1Id = UUID.randomUUID();
        BoardEntity board1Entity = getTestPublicBoard1();
        board1Entity.setId(board1Id);

        UUID board2Id = UUID.randomUUID();
        BoardEntity board2Entity = getTestPrivateBoard1();
        board2Entity.setId(board2Id);

        List<BoardResponseDto> mappedBoards = BoardMapper.toBoardResponseDtoList(Arrays.asList(board1Entity, board2Entity));
        assertEquals(2, mappedBoards.size());

        BoardResponseDto mappedBoard1 = mappedBoards.get(0);
        assertEquals(board1Id, mappedBoard1.getId());
        assertEquals(TEST_PUBLIC_BOARD_1_NAME, mappedBoard1.getName());
        assertEquals(TEST_PUBLIC_BOARD_1_IS_PRIVATE, mappedBoard1.getIsPrivate());
        assertNull(mappedBoard1.getTasks());
        assertNull(mappedBoard1.getChatMessages());

        BoardResponseDto mappedBoard2 = mappedBoards.get(1);
        assertEquals(board2Id, mappedBoard2.getId());
        assertEquals(TEST_PRIVATE_BOARD_1_NAME, mappedBoard2.getName());
        assertEquals(TEST_PRIVATE_BOARD_1_IS_PRIVATE, mappedBoard2.getIsPrivate());
        assertNull(mappedBoard2.getTasks());
        assertNull(mappedBoard2.getChatMessages());
    }

}
