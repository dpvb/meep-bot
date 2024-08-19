package dev.dpvb.mongo.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoService<T> {

    protected final MongoCollection<T> collection;

    MongoService(MongoDatabase database, String collection, Class<T> type) {
        this.collection = database.getCollection(collection, type);
    }

}
