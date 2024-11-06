package fi.dlaitine.task;

import fi.dlaitine.task.board.Application;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {Application.class})
class ApplicationIT {

	@Test
	@DisplayName("Test context initialization")
	void contextLoads() {
	}

}
