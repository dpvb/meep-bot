package dev.dpvb.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.dpvb.mongo.services.MessageService;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.codecs.configuration.CodecRegistry;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.bson.codecs.pojo.PojoCodecProvider.builder;

public class MongoManager {

    private static MongoManager instance;
    private final MessageService messageService;

    private MongoManager() {
        final Dotenv dotenv = Dotenv.load();
        final ConnectionString connectionString = new ConnectionString(dotenv.get("MONGO_URI"));
        final CodecRegistry pojoCodecRegistry = fromRegistries(
                getDefaultCodecRegistry(),
                fromProviders(
                        builder().automatic(true).build()
                )
        );
        final MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(pojoCodecRegistry)
                .build();

        final MongoClient mongoClient = MongoClients.create(clientSettings);
        MongoDatabase db = mongoClient.getDatabase("meep");

        System.out.println("Connected to MongoDB");

        messageService = new MessageService(db);
    }

    public static MongoManager getInstance() {
        if (instance == null) {
            instance = new MongoManager();
        }

        return instance;
    }

    public MessageService getMessageService() {
        return messageService;
    }

}
