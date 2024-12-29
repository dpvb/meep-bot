package dev.dpvb.mongo.models;

import dev.dpvb.mongo.enums.MessageType;

public class MessageStats {

    public String discordID;
    public String username;
    public int totalMessages;
    public int plinks;
    public int mows;
    public int buhs;
    public int humpDays;

    public MessageStats() {

    }

    public MessageStats(String discordID, String username) {
        this(discordID, username, 0, 0, 0, 0, 0);
    }

    public MessageStats(String discordID, String username, int totalMessages, int plinks, int mows, int buhs, int humpDays) {
        this.discordID = discordID;
        this.username = username;
        this.totalMessages = totalMessages;
        this.plinks = plinks;
        this.mows = mows;
        this.buhs = buhs;
        this.humpDays = humpDays;
    }

    public String getDiscordID() {
        return discordID;
    }

    public void setDiscordID(String discordID) {
        this.discordID = discordID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
    }

    public int getPlinks() {
        return plinks;
    }

    public void setPlinks(int plinks) {
        this.plinks = plinks;
    }

    public int getMows() {
        return mows;
    }

    public void setMows(int mows) {
        this.mows = mows;
    }

    public int getBuhs() {
        return buhs;
    }

    public void setBuhs(int buhs) {
        this.buhs = buhs;
    }

    public int getHumpDays() {
        return humpDays;
    }

    public void setHumpDays(int humpDays) {
        this.humpDays = humpDays;
    }

    public void addMessage(MessageType type) {
        // always increment totalMessages
        this.totalMessages++;
        switch (type) {
            case PLINK: {
                this.plinks++;
                break;
            }
            case BUH: {
                this.buhs++;
                break;
            }
            case MOW: {
                this.mows++;
                break;
            }
            case HUMP_DAY: {
                this.humpDays++;
                break;
            }
            case OTHER: {
                // Do nothing else
                break;
            }
        }
    }
}
