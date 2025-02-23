package dev.dpvb.mongo.models;

public class WordleEntry {

    public String discordID;
    public String username;
    public WordleMessageDTO message;

    public WordleEntry() {
    }

    public WordleEntry(String discordID, String username, WordleMessageDTO message) {
        this.discordID = discordID;
        this.username = username;
        this.message = message;
    }

    public String getDiscordID() {
        return this.discordID;
    }

    public void getDiscordID(String discordID) {
        this.discordID = discordID;
    }

    public String getUsername() {
        return this.username;
    }

    public void getUsername(String username) {
        this.username = username;
    }

    public WordleMessageDTO getMessage() {
        return this.message;
    }

    public void getMessage(WordleMessageDTO message) {
        this.message = message;
    }

}
