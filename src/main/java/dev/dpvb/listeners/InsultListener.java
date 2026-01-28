package dev.dpvb.listeners;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.InsultSuggestion;
import dev.dpvb.mongo.services.InsultSuggestionService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class InsultListener extends ListenerAdapter {

    private final InsultSuggestionService iss;
    private static final double INSULT_CHANCE = 0.05;

    private static final long YIP_YAP_CHANNEL_ID = 1466186925030248479L;

    public InsultListener() {
        iss = MongoManager.getInstance().getInsultSuggestionService();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        if (!isValidInsultChannel(event.getChannel().getIdLong())) {
            return;
        }

        if (Math.random() < INSULT_CHANCE) {
            final List<InsultSuggestion> insultsList = iss.getApprovedInsults();
            final String insult = insultsList.get(ThreadLocalRandom.current().nextInt(insultsList.size())).getInsult();
            event.getChannel().sendMessage(insult).queue();
        }
    }

    private boolean isValidInsultChannel(long channelId) {
        // Temporary, boring implementation
        return channelId == YIP_YAP_CHANNEL_ID;
    }
}
