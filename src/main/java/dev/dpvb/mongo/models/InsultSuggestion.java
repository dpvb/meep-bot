package dev.dpvb.mongo.models;

import java.util.Date;

public class InsultSuggestion {

    private String username;
    private String approvalStatus;
    private String insult;
    private Date createdAt;


    public InsultSuggestion() {

    }

    public InsultSuggestion(String username, String approvalStatus, String insult, Date createdAt) {
        this.username = username;
        this.approvalStatus = approvalStatus;
        this.insult = insult;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getInsult() {
        return insult;
    }

    public void setInsult(String insult) {
        this.insult = insult;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
