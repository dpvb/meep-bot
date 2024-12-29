package dev.dpvb.listeners;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.services.MessageStatsService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageStatListener extends ListenerAdapter {

    private final MessageStatsService mss;

    public MessageStatListener() {
         mss = MongoManager.getInstance().getMessageStatsService();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final String username = event.getAuthor().getName();
        final String discordID = event.getAuthor().getId();
        final String message = event.getMessage().getContentRaw();

        if (message.equals("plink")) {
            mss.addPlink(discordID, username);
        } else if (message.equals("buh")) {
            mss.addBuh(discordID, username);
        } else if (message.equalsIgnoreCase("mow")) {
            mss.addMow(discordID, username);
        } else if (message.equals("HUMP DAY")) {
            mss.addHumpDay(discordID, username);
        } else {
            mss.addMessage(discordID, username);
        }
    }
}
