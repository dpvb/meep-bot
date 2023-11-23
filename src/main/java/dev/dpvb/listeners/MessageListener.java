package dev.dpvb.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MessageListener extends ListenerAdapter {

    private final static List<String> INSULTS = List.of(
            "you're so lame",
            "frick you",
            "how can you not fill out a survey properly?",
            "virgin"
    );

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().getId().equals("125063361196064768")) {
            final String insult = INSULTS.get(ThreadLocalRandom.current().nextInt(INSULTS.size()));

            event.getChannel().sendMessage(insult).queue();
        }
    }
}
