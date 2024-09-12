package dev.dpvb.leaderboards;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.services.MessageStatsService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Calendar;


public abstract class Leaderboard {

    private final String LEADERBOARD_TITLE;

    protected MessageStatsService mss;

    public Leaderboard(String title) {
        this.LEADERBOARD_TITLE = "\uD83C\uDFC6 " + title + " \uD83C\uDFC6";
        this.mss = MongoManager.getInstance().getMessageStatsService();
    }

    public MessageEmbed generateEmbed() {
        final EmbedBuilder eb = new EmbedBuilder();

        // Get the time
        eb.setTimestamp(Calendar.getInstance().getTime().toInstant());

        // Set the title
        eb.setTitle(LEADERBOARD_TITLE);

        // Set the ranking
        eb.setDescription(ranking());

        return eb.build();
    }

    protected abstract String ranking();


}
