package dev.dpvb.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class InsultListener extends ListenerAdapter {

    private final List<String> insultsList = Arrays.asList(
            "You're such a rat",
            "Rat",
            "Mow?",
            "I'm so disappointed in you"
    );

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final int random = ThreadLocalRandom.current().nextInt(8);
        if (random == 0) {
            final String insult = insultsList.get(ThreadLocalRandom.current().nextInt(insultsList.size()));
            event.getChannel().sendMessage(insult).queue();
        }
    }
}
