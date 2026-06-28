package io.github.atsin6.codereviewer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import io.github.atsin6.codereviewer.config.MongoTestConfig;

@SpringBootTest
@Import(MongoTestConfig.class)
class CodeReviewerApplicationTests {

	@Test
	void contextLoads() {
	}

}
