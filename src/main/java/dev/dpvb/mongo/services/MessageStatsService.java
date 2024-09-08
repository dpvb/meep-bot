package dev.dpvb.mongo.services;

import com.mongodb.client.MongoDatabase;

import dev.dpvb.mongo.enums.MessageType;
import dev.dpvb.mongo.models.MessageStats;

import static com.mongodb.client.model.Filters.eq;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public void addPlink(String discordId, String username) {
        addOrUpdateType(discordId, username, MessageType.PLINK);
    }

    public void addBuh(String discordId, String username) {
        addOrUpdateType(discordId, username, MessageType.BUH);
    }

    public void addMow(String discordId, String username) {
        addOrUpdateType(discordId, username, MessageType.MOW);
    }

    public void addMessage(String discordId, String username) {
        addOrUpdateType(discordId, username, MessageType.OTHER);
    }

    private void addOrUpdateType(String discordID, String username, MessageType type) {
        MessageStats messageStats = collection.find(eq("username", username)).first();
        if (messageStats == null) {
            messageStats = new MessageStats(discordID, username);
            messageStats.addMessage(type);
            collection.insertOne(messageStats);
        } else {
            messageStats.addMessage(type);
            collection.replaceOne(eq("username", username), messageStats);
        }
    }

    public MessageStats getUserMessageStats(String username) {
        return collection.find(eq("username", username)).first();
    }

    public List<MessageStats> getAllMessageStats() {
        return StreamSupport.stream(collection.find().spliterator(), false).collect(Collectors.toList());
    }

}
