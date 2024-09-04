package dev.dpvb.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.mongo.models.InsultSuggestion;
import org.bson.BsonValue;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

public class InsultSuggestionService extends MongoService<InsultSuggestion> {

    public InsultSuggestionService(MongoDatabase database) {
        super(database, "insult-suggestions", InsultSuggestion.class);
    }

    public BsonValue addInsultSuggestion(InsultSuggestion insultSuggestion) {
        return collection.insertOne(insultSuggestion).getInsertedId();
    }

    public void updateInsultSuggestion(String id, InsultSuggestion insultSuggestion) {
        collection.replaceOne(eq("_id", new ObjectId(id)), insultSuggestion);
    }

    public InsultSuggestion getInsultSuggestion(String id) {
        return collection.find(eq("_id", new ObjectId(id))).first();
    }

    public List<InsultSuggestion> getApprovedInsults() {
        return StreamSupport.stream(collection.find(eq("approvalStatus", "approved")).spliterator(), false).collect(Collectors.toList());
    }

}
