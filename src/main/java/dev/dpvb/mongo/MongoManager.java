package dev.dpvb.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.dpvb.MeepBot;
import dev.dpvb.mongo.services.InsultSuggestionService;
import dev.dpvb.mongo.services.MessageStatsService;
import dev.dpvb.mongo.services.WordleEntryService;
import org.bson.codecs.configuration.CodecRegistry;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.bson.codecs.pojo.PojoCodecProvider.builder;

public class MongoManager {

    private static MongoManager instance;
    private final MessageStatsService messageStatsService;
    private final InsultSuggestionService insultSuggestionService;
    private final WordleEntryService wordleEntryService;

    private MongoManager() {
        final ConnectionString connectionString = new ConnectionString(MeepBot.Environment.getMongoURI());
        final CodecRegistry pojoCodecRegistry = fromRegistries(
                getDefaultCodecRegistry(),
                fromProviders(
                        builder().automatic(true).build()));

        final MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(pojoCodecRegistry)
                .build();

        final MongoClient mongoClient = MongoClients.create(clientSettings);
        MongoDatabase db = mongoClient.getDatabase("meep");

        System.out.println("Connected to MongoDB");

        messageStatsService = new MessageStatsService(db);
        insultSuggestionService = new InsultSuggestionService(db);
        wordleEntryService = new WordleEntryService(db);
    }

    public static MongoManager getInstance() {
        if (instance == null) {
            instance = new MongoManager();
        }

        return instance;
    }

    public MessageStatsService getMessageStatsService() {
        return messageStatsService;
    }

    public InsultSuggestionService getInsultSuggestionService() {
        return insultSuggestionService;
    }

    public WordleEntryService getWordleEntryService() {
        return wordleEntryService;
    }
}
