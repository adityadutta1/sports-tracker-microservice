package com.example.sports_tracker.service;

import com.example.sports_tracker.KafkaPublisher;
import com.example.sports_tracker.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

@Service
@Slf4j
public class EventService {

    @Autowired
    private KafkaPublisher kafkaPublisher;

    @Autowired
    private RestTemplate restTemplate;

    // In-memory storage
    private final Map<String, Event> events = new ConcurrentHashMap<>();

    // Scheduler for 10-second tasks
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    // Track running tasks
    private final Map<String, ScheduledFuture<?>> runningTasks = new ConcurrentHashMap<>();

    private final Random random = new Random();

    public void updateEventStatus(String eventId, boolean isLive) {
        Event event = events.computeIfAbsent(eventId, id -> new Event(id, false));
        boolean wasLive = event.isLive();
        event.setLive(isLive);

        if (isLive && !wasLive) {
            // Start tracking
            startTracking(eventId);
        } else if (!isLive && wasLive) {
            // Stop tracking
            stopTracking(eventId);
        }
    }

    private void startTracking(String eventId) {
        stopTracking(eventId); // Stop existing task if any

        log.info("Starting tracking for event: {}", eventId);

        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(
                () -> fetchAndPublish(eventId),
                0, 10, TimeUnit.SECONDS
        );

        runningTasks.put(eventId, task);
    }

    private void stopTracking(String eventId) {
        ScheduledFuture<?> task = runningTasks.remove(eventId);
        if (task != null) {
            task.cancel(false);
            log.info("Stopped tracking for event: {}", eventId);
        }
    }

    private void fetchAndPublish(String eventId) {
        try {
            log.debug("Fetching data for event: {}", eventId);

            // Call external API (with retry logic)
            SportsData data = callExternalApi(eventId);

            if (data != null) {
                // Transform and publish
                Map<String, Object> message = new HashMap<>();
                message.put("eventId", data.eventId);
                message.put("currentScore", data.currentScore);
                message.put("timestamp", System.currentTimeMillis());

                kafkaPublisher.publishMessage("sports-events", eventId, message);
                log.debug("Published data for event: {}", eventId);
            }

        } catch (Exception e) {
            log.error("Error processing event: {}", eventId, e);
        }
    }

    private SportsData callExternalApi(String eventId) {
        try {
            // Mock external API call (replace with real API)
            String score = generateMockScore();
            return new SportsData(eventId, score);

            // Real API call would be:
            // ResponseEntity<SportsData> response = restTemplate.getForEntity(
            //     "http://external-api.com/events/" + eventId, SportsData.class);
            // return response.getBody();

        } catch (Exception e) {
            log.error("External API call failed for event: {}", eventId, e);
            throw e;
        }
    }

    private String generateMockScore() {
        return random.nextInt(4) + ":" + random.nextInt(4);
    }

    public static class SportsData {
        public String eventId;
        public String currentScore;

        public SportsData() {}
        public SportsData(String eventId, String currentScore) {
            this.eventId = eventId;
            this.currentScore = currentScore;
        }
    }
}