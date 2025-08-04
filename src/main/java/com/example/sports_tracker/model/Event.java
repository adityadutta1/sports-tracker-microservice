package com.example.sports_tracker.model;

public class Event {
    private String eventId;
    private boolean live;

    public Event(String eventId, boolean live) {
        this.eventId = eventId;
        this.live = live;
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public boolean isLive() { return live; }
    public void setLive(boolean live) { this.live = live; }
}