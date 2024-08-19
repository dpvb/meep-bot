package dev.dpvb.mongo.models;

public class Message {

    private String username;
    private String message;
    private String timeCreated;

    public Message(String username, String message, String timeCreated) {
        this.username = username;
        this.message = message;
        this.timeCreated = timeCreated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }
}
