package com.tnc.shelter;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Application context test disabled - requires external services (Eureka, Config Server, RabbitMQ)")
class ShelterApplicationTests {

	@Test
	void contextLoads() {
	}

}
