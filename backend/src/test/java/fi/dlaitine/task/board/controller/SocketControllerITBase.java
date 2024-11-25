package fi.dlaitine.task.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.dlaitine.task.board.entity.BoardEntity;
import fi.dlaitine.task.board.repository.BoardRepository;
import fi.dlaitine.task.board.repository.ChatMessageRepository;
import fi.dlaitine.task.board.repository.TaskRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
abstract class SocketControllerITBase {

    // SOCKET PATHS
    protected static final String WEBSOCKET_APP = "/app";
    protected static final String WEBSOCKET_TOPIC = "/topic";
    protected static final String ERROR_LISTEN_PATH = "/user/queue/error";

    protected UUID testBoardId;

    protected StompSession stompSession;

    @LocalServerPort
    protected Integer port;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected BoardRepository boardRepository;

    @Autowired
    protected TaskRepository taskRepository;

    @Autowired
    protected ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void setup() throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(messageConverter);

        stompSession = stompClient
                .connectAsync(getWebSocketUrl(), new StompSessionHandlerAdapter() {})
                .get(5, SECONDS);
    }

    @AfterEach
    void teardown() {
        stompSession.disconnect();
        taskRepository.deleteAll();
        chatMessageRepository.deleteAll();
        boardRepository.deleteAll();
    }

    protected String getWebSocketUrl() {
        return "http://localhost:" + port + "/ws";
    }

    protected void initializeTestBoard() {
        BoardEntity board = BoardEntity.builder()
                .name("Test Board")
                .isPrivate(false)
                .build();

        board = boardRepository.save(board);
        testBoardId = board.getId();
    }

}
