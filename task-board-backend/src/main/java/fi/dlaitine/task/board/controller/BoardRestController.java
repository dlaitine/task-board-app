package fi.dlaitine.task.board.controller;

import fi.dlaitine.task.board.dto.BoardResponseDto;
import fi.dlaitine.task.board.dto.CreateBoardDto;
import fi.dlaitine.task.board.exception.BoardNotFoundException;
import fi.dlaitine.task.board.service.BoardService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/boards")
public class BoardRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoardRestController.class);

    private final BoardService boardService;

    @Autowired
    public BoardRestController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("")
    public ResponseEntity<List<BoardResponseDto>> getPublicBoards() {
        return ResponseEntity.ok(boardService.getPublicBoards());
    }

    @GetMapping(value = "/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoard(
            @PathVariable UUID boardId,
            @RequestParam(value = "includeTasks", required = false, defaultValue = "false") boolean includeTasks,
            @RequestParam(value = "includeChatMessages", required = false, defaultValue = "false") boolean includeChatMessages) throws BoardNotFoundException {
        return ResponseEntity.ok(boardService.getBoard(boardId, includeTasks, includeChatMessages));
    }

    @PostMapping("")
    public ResponseEntity<BoardResponseDto> createBoard(@Valid @RequestBody CreateBoardDto createBoardDto) {
        BoardResponseDto createdBoard = boardService.createBoard(createBoardDto);

        URI newResourceUrl = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBoard.getId())
                .toUri();

        return ResponseEntity.created(newResourceUrl)
                .body(createdBoard);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOGGER.warn("Invalid method argument when fetching board: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        LOGGER.warn("Invalid method argument type when fetching board: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<Void> handleBoardNotFoundException(BoardNotFoundException e) {
        LOGGER.warn("Exception when fetching board: {}", e.getMessage(), e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException(Exception e) {
        LOGGER.warn("Exception was thrown during board request: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().build();
    }


}
