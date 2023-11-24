package dev.dpvb.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MessageListener extends ListenerAdapter {

    private final static List<String> INSULTS = List.of(
            "you're so lame",
            "frick you",
            "how can you not fill out a survey properly?",
            "so cap",
            "..."
    );

    private final static List<String> INSULTEES = List.of(
            "125063361196064768",   // flare
            "332537342705401856"    // rrisky
    );

    private final static int ODDS = 5; // Chance of sending an insult 1 / ODDS

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (ThreadLocalRandom.current().nextInt(ODDS) != 0) {
            return;
        }

        if (INSULTEES.contains(event.getAuthor().getId())) {
            final String insult = INSULTS.get(ThreadLocalRandom.current().nextInt(INSULTS.size()));
            event.getChannel().sendMessage(insult).queue();
        }
    }
}
