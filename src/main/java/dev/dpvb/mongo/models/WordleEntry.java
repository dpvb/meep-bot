package dev.dpvb.mongo.models;

import dev.dpvb.wordle.WordleMessage;

public class WordleEntry {

    public String discordID;
    public String username;
    public WordleMessage message;

    public WordleEntry() {
    }

    public WordleEntry(String discordID, String username, WordleMessage message) {
        this.discordID = discordID;
        this.username = username;
        this.message = message;
    }

    public String getDiscordID() {
        return this.discordID;
    }

    public void setDiscordID(String discordID) {
        this.discordID = discordID;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public WordleMessage getMessage() {
        return this.message;
    }

    public void setMessage(WordleMessage message) {
        this.message = message;
    }

}
