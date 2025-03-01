package dev.dpvb.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.mongo.models.WordleEntry;
import dev.dpvb.wordle.WordleMessage;

import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

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

    public List<WordleEntry> getEntriesByWordleNumberOrderByGuessCount(int wordleNumber) {
        return getEntriesByWordleNumberOrderByGuessCount(wordleNumber, true);
    }

    public List<WordleEntry> getEntriesByWordleNumberOrderByGuessCount(int wordleNumber, boolean winnersFirst) {
        final Bson filter = eq("message.wordleNumber", wordleNumber);

        Function<WordleEntry, Integer> fieldExtractor = e -> e.getMessage().getGuessCount();
        if (winnersFirst) {
            fieldExtractor = fieldExtractor.andThen(wc -> wc == WordleMessage.LOSE_COUNT ? Integer.MAX_VALUE : wc);
        }

        List<WordleEntry> entries = collection.find(filter).into(new ArrayList<>());
        entries.sort(Comparator.comparing(fieldExtractor));

        return entries;
    }

    public List<WordleEntry> getEntriesByDiscordID(String discordID) {
        Bson filter = eq("discordID", discordID);
        return collection.find(filter).into(new ArrayList<>());
    }

}
