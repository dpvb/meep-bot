package dev.dpvb.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.mongo.models.WordleEntry;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class WordleEntryService extends MongoService<WordleEntry> {

    public WordleEntryService(MongoDatabase database) {
        super(database, "wordle-entries", WordleEntry.class);
    }

    public void addOrUpdateEntry(WordleEntry entry) {
        Bson filter = and(eq("discordID", entry.discordID), eq("message.wordleNumber", entry.message.wordleNumber));

        WordleEntry findRes = collection.find(filter).first();

        if (findRes == null) {
            collection.insertOne(entry);
        } else {
            collection.replaceOne(filter, entry);
        }
    }

}
