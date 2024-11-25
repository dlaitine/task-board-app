package fi.dlaitine.task.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.dlaitine.task.board.dto.CreateBoardDto;
import fi.dlaitine.task.board.service.BoardService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardRestController.class)
class BoardRestControllerTest {

    @MockBean
    private BoardService boardService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test fetching public boards when service throws an exception, should return internal server error")
    void test_when_getPublicBoards_andServiceThrowsRuntimeException_shouldReturnInternalError() throws Exception {
        when(boardService.getPublicBoards()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/boards"))
                        .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Test fetching board when service throws an exception, should return internal server error")
    void test_when_getBoard_andServiceThrowsRuntimeException_shouldReturnInternalError() throws Exception {
        UUID testBoardId = UUID.randomUUID();
        when(boardService.getBoard(testBoardId, false, false)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/boards/" + testBoardId))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Test creating a new board when service throws an exception, should return internal server error")
    void test_when_createNewBoard_andServiceThrowsRuntimeException_shouldReturnInternalError() throws Exception {
        CreateBoardDto newBoard = new CreateBoardDto("Test Board", false);

        when(boardService.createBoard(newBoard)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/v1/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBoard)))
                .andExpect(status().is5xxServerError());
    }

}
