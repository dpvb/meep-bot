package dev.dpvb.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.mongo.models.WordleEntry;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class WordleEntryService extends MongoService<WordleEntry> {

    public WordleEntryService(MongoDatabase database) {
        super(database, "wordle-entries", WordleEntry.class);
    }

    public void addOrUpdateEntry(WordleEntry entry) {
        Bson filter = and(
                eq("discordID", entry.discordID),
                eq("message.wordleNumber", entry.message.getWordleNumber()));

        WordleEntry findRes = collection.find(filter).first();

        if (findRes == null) {
            collection.insertOne(entry);
        } else {
            collection.replaceOne(filter, entry);
        }
    }

    public List<WordleEntry> getEntriesByWordleNumber(int wordleNumber) {
        final Bson filter = eq("message.wordleNumber", wordleNumber);
        return collection.find(filter).into(new ArrayList<>());
    }

}
