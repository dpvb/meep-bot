package dev.dpvb.listeners;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.services.MessageStatsService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.DayOfWeek;

public class MessageStatListener extends ListenerAdapter {

    private final MessageStatsService mss;

    public MessageStatListener() {
         mss = MongoManager.getInstance().getMessageStatsService();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final String username = event.getAuthor().getName();
        final String discordID = event.getAuthor().getId();
        final Message message = event.getMessage();
        final String content = message.getContentRaw();

        if (content.equals("plink")) {
            mss.addPlink(discordID, username);
        } else if (content.equals("buh")) {
            mss.addBuh(discordID, username);
        } else if (content.equalsIgnoreCase("mow")) {
            mss.addMow(discordID, username);
        } else if (content.equals("HUMP DAY")
                && message.getTimeCreated().getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
            mss.addHumpDay(discordID, username);
        } else {
            mss.addMessage(discordID, username);
        }
    }
}
