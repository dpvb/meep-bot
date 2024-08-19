package dev.dpvb.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.mongo.models.Message;

public class MessageService extends MongoService<Message> {

    public MessageService(MongoDatabase database) {
        super(database, "messages", Message.class);
    }

    public void create(Message message) {
        collection.insertOne(message);
    }

}
