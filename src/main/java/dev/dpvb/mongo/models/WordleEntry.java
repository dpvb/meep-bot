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

    public String asShortMessage(boolean withHeader) {
        return this.asShortMessage(withHeader, "");
    }

    public String asShortMessage(boolean withHeader, String prefix) {
        String headerString = withHeader ? formatHeader() : "";
        String numberString = this.message.getGuessCount() < 0 ? "X" : Integer.toString(this.message.getGuessCount());
        String hardModeString = this.message.isHardMode() ? "*" : "";

        return String.format("%s%s**%s** %s/6%s",
                headerString,
                prefix,
                this.getUsername(),
                numberString,
                hardModeString);
    }

    public String asLongMessage(boolean withHeader) {
        String headerString = withHeader ? formatHeader() : "";
        String numberString = this.message.getGuessCount() < 0 ? "X" : Integer.toString(this.message.getGuessCount());
        String hardModeString = this.message.isHardMode() ? "*" : "";

        return String.format("%s**%s** %s/6%s%n%s",
                headerString,
                this.username,
                numberString,
                hardModeString,
                this.message.toGuessString());
    }

    private String formatHeader() {
        return String.format("Wordle %,d%n", this.getMessage().getWordleNumber());
    }

}
