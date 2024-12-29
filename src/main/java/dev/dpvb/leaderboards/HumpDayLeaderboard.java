package dev.dpvb.leaderboards;

import dev.dpvb.mongo.models.MessageStats;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HumpDayLeaderboard extends Leaderboard {

    public HumpDayLeaderboard() {
        super("HUMP DAY LEADERBOARD");
    }

    @Override
    protected String ranking() {
        final List<MessageStats> top = mss.getAllMessageStats().stream()
                .sorted(Comparator.comparing(MessageStats::getHumpDays).reversed())
                .limit(10)
                .collect(Collectors.toList());

        final StringBuilder sb = new StringBuilder();
        top.forEach(user -> sb.append(String.format("<@%s> - %d\n", user.getDiscordID(), user.getHumpDays())));
        return sb.toString();
    }
}
