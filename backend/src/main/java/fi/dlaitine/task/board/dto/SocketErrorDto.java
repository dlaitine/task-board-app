package fi.dlaitine.task.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SocketErrorDto(@JsonProperty("error_msg") String errorMsg) {
}
