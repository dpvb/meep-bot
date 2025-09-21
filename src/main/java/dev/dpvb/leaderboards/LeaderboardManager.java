package dev.dpvb.leaderboards;

import dev.dpvb.util.TimeUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LeaderboardManager {

    private static LeaderboardManager INSTANCE;

    private final List<Leaderboard> leaderboards;

    private static final String LEADERBOARD_MESSAGE_ID = "1283851549113712660";
    private static final String LEADERBOARD_CHANNEL_ID = "1282365851001163786";

    private final TextChannel leaderboardChannel;

    private static final long UPDATE_INTERVAL = 60L; // Time in Minutes

    private LeaderboardManager(JDA jda) {
        this.leaderboardChannel = jda.getTextChannelById(LEADERBOARD_CHANNEL_ID);

        leaderboards = new ArrayList<>();
        leaderboards.add(new PlinkLeaderboard());
        leaderboards.add(new BuhLeaderboard());
        leaderboards.add(new MowLeaderboard());
        leaderboards.add(new HumpDayLeaderboard());
        leaderboards.add(new JoshJoinCountLeaderboard(jda));

        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(
                this::updateLeaderboards,
                TimeUtil.timeToNextHour(),
                UPDATE_INTERVAL * 60L * 1000L,
                TimeUnit.MILLISECONDS
        );
    }

    public static void init(JDA jda) {
        if (INSTANCE == null) {
            INSTANCE = new LeaderboardManager(jda);
        }
    }

    private void updateLeaderboards() {
        List<MessageEmbed> embeds = leaderboards.stream().map(Leaderboard::generateEmbed).collect(Collectors.toList());
        leaderboardChannel.editMessageEmbedsById(LEADERBOARD_MESSAGE_ID, embeds).queue();
    }


}
