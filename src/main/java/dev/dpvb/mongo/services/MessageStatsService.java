package dev.dpvb.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.mongo.models.MessageStats;

import static com.mongodb.client.model.Filters.eq;

public class MessageStatsService extends MongoService<MessageStats> {

    public MessageStatsService(MongoDatabase database) {
        super(database, "message-stats", MessageStats.class);
    }

    public void addPlink(String username) {
        MessageStats messageStats = collection.find(eq("username", username)).first();
        if (messageStats == null) {
            messageStats = new MessageStats(username, 1, 1, 0, 0);
            collection.insertOne(messageStats);
        } else {
            messageStats.totalMessages++;
            messageStats.plinks++;
            collection.replaceOne(eq("username", username), messageStats);
        }
    }

    public void addBuh(String username) {
        MessageStats messageStats = collection.find(eq("username", username)).first();
        if (messageStats == null) {
            messageStats = new MessageStats(username, 1, 0, 0, 1);
            collection.insertOne(messageStats);
        } else {
            messageStats.totalMessages++;
            messageStats.buhs++;
            collection.replaceOne(eq("username", username), messageStats);
        }
    }

    public void addMow(String username) {
        MessageStats messageStats = collection.find(eq("username", username)).first();
        if (messageStats == null) {
            messageStats = new MessageStats(username, 1, 0, 1, 0);
            collection.insertOne(messageStats);
        } else {
            messageStats.totalMessages++;
            messageStats.mows++;
            collection.replaceOne(eq("username", username), messageStats);
        }
    }

    public void addMessage(String username) {
        MessageStats messageStats = collection.find(eq("username", username)).first();
        if (messageStats == null) {
            messageStats = new MessageStats(username, 1, 0, 0, 0);
            collection.insertOne(messageStats);
        } else {
            messageStats.totalMessages++;
            collection.replaceOne(eq("username", username), messageStats);
        }
    }

}
