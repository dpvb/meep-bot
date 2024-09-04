package dev.dpvb.listeners;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.InsultSuggestion;
import dev.dpvb.mongo.services.InsultSuggestionService;
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
            "I'm so disappointed in you",
            "Have you been a bad boy???",
            "I bet you go to bed with socks on",
            "Very mowful, very demure",
            "i bet you still display your kindergarten participation trophies",
            "ur feet stink",
            "ur parents still leash u when they go out with u?",
            "ur not funny",
            "still sippin appy juice?",
            "bro gets starbucks with their rewards",
            "i bet ur sticky notes aren't even sticky",
            "probably sleeps with fan on ngl",
            "probably couldn't rawdog a 12 hour flight",
            "u need a fork for your chopsticks",
            "i bet u only clean ur bathroom once a month",
            "probably puts on underwear after the pants",
            "i bet u like cookies over brownies",
            "this guy got a headphone dent for sure",
            "*teleports behind you*",
            "i bet u thought the dress was white and not blue",
            "i bet ur not even sad hamster",
            "bro witnessed and saw square",
            "cope, seethe, mald",
            "u let rrisky out mow you????",
            "bro is probably a g2 fan",
            "prolly a country music enjoyer this one, yikes...",
            "just do it, more like, just can't do it",
            "bro is vampire but no fangs",
            "all yap no bite",
            "not a grass enjoyer i c",
            "whens the last time sunlight touched you",
            "bro has 2 guest houses",
            "bro has 4 monitors",
            "probably watches tiktok in landscape",
            "i bet u scream chat clip that",
            "probably uses clip software",
            "bro screams chat, chat, chat",
            "bro probably has a job ngl",
            "bro is probably in a loving and caring relationship",
            "its past your bedtime sleepyhead..."
    );

    private final InsultSuggestionService iss;

    public InsultListener() {
        iss = MongoManager.getInstance().getInsultSuggestionService();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        if (Math.random() < 0.05) {
            final List<InsultSuggestion> insultsList = iss.getApprovedInsults();
            final String insult = insultsList.get(ThreadLocalRandom.current().nextInt(insultsList.size())).getInsult();
            event.getChannel().sendMessage(insult).queue();
        }
    }
}
