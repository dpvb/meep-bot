package dev.dpvb.leaderboards;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.MessageStats;
import dev.dpvb.mongo.services.MessageStatsService;
import dev.dpvb.util.TimeUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class PlinkLeaderboard {

    private static final String LEADERBOARD_CHANNEL_ID = "1282365851001163786";
    private static final String LEADERBOARD_TITLE = "\uD83C\uDFC6 PLINK LEADERBOARD \uD83C\uDFC6";
    private static final String LEADERBOARD_MESSAGE_ID = "1282374559122722826";
    private static final long UPDATE_INTERVAL = 1L; // Time in Minutes

    private MessageStatsService mss;
    private TextChannel leaderboardChannel;


    public PlinkLeaderboard(JDA jda) {
        this.mss = MongoManager.getInstance().getMessageStatsService();
        this.leaderboardChannel = jda.getTextChannelById(LEADERBOARD_CHANNEL_ID);

        Timer timer = new Timer();
        timer.schedule(new UpdateTask(), TimeUtil.timeToNextHour(), UPDATE_INTERVAL * 1000 * 60);
    }

    private void updateLeaderboard() {
        MessageEmbed messageEmbed = generateEmbed();
        leaderboardChannel.editMessageEmbedsById(LEADERBOARD_MESSAGE_ID, messageEmbed).queue();
        System.out.println("Updated Plink Leaderboard at " + Calendar.getInstance().getTime());
    }

    private MessageEmbed generateEmbed() {
        EmbedBuilder eb = new EmbedBuilder();

        // Get the time
        // String formattedDate = LocalTime.now().format(DATE_TIME_FORMATTER);
        eb.setTimestamp(Calendar.getInstance().getTime().toInstant());

        // Set the Title
        eb.setTitle(LEADERBOARD_TITLE);

        // Set the Description
        getTopPlinkers().forEach(user -> {
            eb.appendDescription(String.format("<@%s> - %d\n", user.getDiscordID(), user.getPlinks()));
        });

        return eb.build();
    }

    private List<MessageStats> getTopPlinkers() {
        return mss.getAllMessageStats().stream()
                .sorted((a, b) -> b.getPlinks() - a.getPlinks())
                .limit(10)
                .collect(Collectors.toList());
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {
            updateLeaderboard();
        }
    }

}
