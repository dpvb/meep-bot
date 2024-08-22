package dev.dpvb.mongo.services;

import com.mongodb.client.MongoDatabase;

import dev.dpvb.mongo.enums.MessageType;
import dev.dpvb.mongo.models.MessageStats;

import static com.mongodb.client.model.Filters.eq;

public class MessageStatsService extends MongoService<MessageStats> {

    public MessageStatsService(MongoDatabase database) {
        super(database, "message-stats", MessageStats.class);
    }

    public void setStats(MessageStats newStats) {
        MessageStats messageStats = collection.find(eq("username", newStats.username)).first();
        if (messageStats == null) {
            collection.insertOne(newStats);
        } else {
            collection.replaceOne(eq("username", newStats.username), newStats);
        }
    }

    public void addPlink(String username) {
        addOrUpdateType(username, MessageType.PLINK);
    }

    public void addBuh(String username) {
        addOrUpdateType(username, MessageType.BUH);
    }

    public void addMow(String username) {
        addOrUpdateType(username, MessageType.MOW);
    }

    public void addMessage(String username) {
        addOrUpdateType(username, MessageType.OTHER);
    }

    private void addOrUpdateType(String username, MessageType type) {
        MessageStats messageStats = collection.find(eq("username", username)).first();
        if (messageStats == null) {
            messageStats = new MessageStats(username);
            messageStats.addMessage(type);
            collection.insertOne(messageStats);
        } else {
            messageStats.addMessage(type);
            collection.replaceOne(eq("username", username), messageStats);
        }
    }

}
