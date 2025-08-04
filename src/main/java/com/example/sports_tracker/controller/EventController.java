package com.example.sports_tracker.controller;

import com.example.sports_tracker.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/events")
@Slf4j
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/status")
    public ResponseEntity<Map<String, Object>> updateEventStatus(@RequestBody EventStatusRequest request) {
        try {
            log.info("Event status update: eventId={}, status={}", request.eventId, request.status);

            eventService.updateEventStatus(request.eventId, request.status);

            return ResponseEntity.ok(Map.of(
                    "eventId", request.eventId,
                    "status", request.status ? "live" : "not live",
                    "message", "Status updated successfully"
            ));
        } catch (Exception e) {
            log.error("Error updating event status", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public static class EventStatusRequest {
        public String eventId;
        public boolean status;
    }
}