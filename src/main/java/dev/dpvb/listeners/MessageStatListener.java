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
        final String message = event.getMessage().getContentRaw();

        if (message.equals("plink")) {
            mss.addPlink(username);
        } else if (message.equals("buh")) {
            mss.addBuh(username);
        } else if (message.equalsIgnoreCase("mow")) {
            mss.addMow(username);
        } else {
            mss.addMessage(username);
        }
    }
}
