package com.tnc.userManagement;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled("Application context test disabled - requires external services (Eureka, Config Server, Database)")
@SpringBootTest
@ActiveProfiles("test")
class UserManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
