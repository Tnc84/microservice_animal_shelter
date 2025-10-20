package com.tnc.animals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Application context test disabled - requires external services (Eureka, Config Server, Database)")
@SpringBootTest
class AnimalsApplicationTests {

	@Test
	void contextLoads() {
	}

}
