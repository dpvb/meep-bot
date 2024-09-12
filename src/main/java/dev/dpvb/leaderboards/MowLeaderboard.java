package dev.dpvb.leaderboards;

import dev.dpvb.mongo.models.MessageStats;

import java.util.List;
import java.util.stream.Collectors;

public class MowLeaderboard extends Leaderboard {

    public MowLeaderboard() {
        super("MOW LEADERBOARD");
    }

    @Override
    public String ranking() {
        final List<MessageStats> top = mss.getAllMessageStats().stream()
                .sorted((a, b) -> b.getMows() - a.getMows())
                .limit(10)
                .collect(Collectors.toList());

        final StringBuilder sb = new StringBuilder();
        top.forEach(user -> sb.append(String.format("<@%s> - %d\n", user.getDiscordID(), user.getMows())));
        return sb.toString();
    }
}
