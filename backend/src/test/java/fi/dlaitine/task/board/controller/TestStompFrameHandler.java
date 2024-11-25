package fi.dlaitine.task.board.controller;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import static java.util.concurrent.TimeUnit.SECONDS;

class TestStompFrameHandler<T> implements StompFrameHandler {

    private final BlockingQueue<T> blockingQueue;

    private final Class<T> clazz;

    public TestStompFrameHandler(Class<T> clazz) {
        blockingQueue = new LinkedBlockingDeque<>();
        this.clazz = clazz;
    }
    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        blockingQueue.add((T) o);
    }

    public T poll(int timeoutSeconds) throws InterruptedException {
        return blockingQueue.poll(timeoutSeconds, SECONDS);
    }
}