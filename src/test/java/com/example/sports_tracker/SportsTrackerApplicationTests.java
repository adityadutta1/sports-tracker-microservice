package com.example.sports_tracker;

import com.example.sports_tracker.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
		"app.kafka.enabled=false",
		"app.external-api.mock=true"
})
public class SportsTrackerApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private EventService eventService;

	@Test
	void contextLoads() {
		// Basic context loading test
	}

	@Test
	void testEventStatusUpdate() throws Exception {
		String url = "http://localhost:" + port + "/events/status";

		Map<String, Object> request = Map.of(
				"eventId", "TEST001",
				"status", true
		);

		ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("TEST001", response.getBody().get("eventId"));
	}

	@Test
	void testEventServiceDirectly() {
		// Test service logic directly
		assertDoesNotThrow(() -> {
			eventService.updateEventStatus("TEST002", true);
			Thread.sleep(2000); // Let it run for 2 seconds
			eventService.updateEventStatus("TEST002", false);
		});
	}
}