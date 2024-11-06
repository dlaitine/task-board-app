package fi.dlaitine.task.board.service;

import fi.dlaitine.task.board.dto.BoardResponseDto;
import fi.dlaitine.task.board.dto.CreateBoardDto;
import fi.dlaitine.task.board.exception.BoardNotFoundException;
import fi.dlaitine.task.board.mapper.BoardMapper;
import fi.dlaitine.task.board.repository.BoardRepository;
import fi.dlaitine.task.board.entity.BoardEntity;
import fi.dlaitine.task.board.mapper.ChatMessageMapper;
import fi.dlaitine.task.board.mapper.TaskMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoardService.class);

    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository repository) {
        this.boardRepository = repository;
    }

    public List<BoardResponseDto> getPublicBoards() {
        List<BoardEntity> publicBoards = boardRepository.findByIsPrivateFalse();
        return BoardMapper.toBoardResponseDtoList(publicBoards);
    }

    @Transactional
    public BoardResponseDto getBoard(UUID boardId, boolean includeTasks, boolean includeChatMessages) throws BoardNotFoundException {
        Optional<BoardEntity> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new BoardNotFoundException("Board with id " + boardId + " not found.");
        }

        BoardEntity boardEntity = optionalBoard.get();

        BoardResponseDto boardDto = BoardMapper.toBoardResponseDto(boardEntity);

        if (includeTasks) {
            boardDto.setTasks(TaskMapper.toTaskResponseDtoList(boardEntity.getTasks()));
        }

        if (includeChatMessages) {
            boardDto.setChatMessages(ChatMessageMapper.toChatMessageResponseDtoList(boardEntity.getChatMessages()));
        }

        return boardDto;
    }

    public BoardResponseDto createBoard(CreateBoardDto board) {
        BoardEntity boardEntity = BoardMapper.toBoardEntity(board);

        try {
            boardEntity = boardRepository.save(boardEntity);
        } catch (DataIntegrityViolationException e) {
            LOGGER.warn("Exception was thrown when saving new board {} to database: {}", board, e.getMessage(), e);
            throw e;
        }

        return BoardMapper.toBoardResponseDto(boardEntity);
    }
}
