package com.develop.auth_microservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;



@SpringBootTest(properties = {
		"server.port=8082", // O cualquier puerto disponible
		"spring.datasource.url=jdbc:postgresql://ep-dark-mouse-a4irs4kd-pooler.us-east-1.aws.neon.tech/trackitdb",
		"spring.datasource.username=trackitdb_owner",
		"spring.datasource.password=npg_QzrIC3mx1wiE",
		"jwt.secret.key=It-UDfOa3Fohvjk7xHzbER1wLUoAz0i5p4_zn-YxvD0"
})

class AuthMicroserviceApplicationTests {

	@Test
	void contextLoads() {
	}

}
