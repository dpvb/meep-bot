package dev.dpvb.mongo.models;

import dev.dpvb.mongo.enums.MessageType;

public class MessageStats {

    public String username;
    public int totalMessages;
    public int plinks;
    public int mows;
    public int buhs;

    public MessageStats() {

    }

    public MessageStats(String username) {
        this(username, 0, 0, 0, 0);
    }

    public MessageStats(String username, int totalMessages, int plinks, int mows, int buhs) {
        this.username = username;
        this.totalMessages = totalMessages;
        this.plinks = plinks;
        this.mows = mows;
        this.buhs = buhs;
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
            case OTHER: {
                // Do nothing else
                break;
            }
        }
    }
}
