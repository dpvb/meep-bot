package dev.dpvb.jobs;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class WordleWinnerJob extends Job {

    private JDA jda;
    private static final long wordleChannelId = 1326175839884148867L;

    public WordleWinnerJob(JDA jda) {
        this.jda = jda;
    }

    public void run() {
        final TextChannel wordleChannel = jda.getTextChannelById(wordleChannelId);
        if (wordleChannel == null) {
            System.out.println("Error: Could not retrieve Wordle Channel");
            return;
        }

        final OffsetDateTime yesterday = OffsetDateTime.now().minusDays(1);

        final List<Message> lastDayWordleSubmissions = wordleChannel.getIterableHistory()
                .stream()
                .filter(message -> message.getTimeCreated().isAfter(yesterday) && isWordleSubmission(message.getContentRaw()))
                .collect(Collectors.toList());

        final Map<User, Integer> scores = new HashMap<>();

        lastDayWordleSubmissions.forEach(submission -> {
            scores.put(submission.getAuthor(), getScore(submission.getContentRaw()));
        });

        List<Map.Entry<User, Integer>> lowestScores = getLowestScores(scores);

        if (lowestScores == null || lowestScores.isEmpty()) {
            wordleChannel.sendMessage("No one submitted anything for the Wordle today :C").queue();
            return;
        }

        final String msgTitle = "\uD83C\uDFC6 **Wordle Winners Today** \uD83C\uDFC6";
        final StringBuilder sb = new StringBuilder();
        lowestScores.forEach(scoreEntry -> {
            sb.append("\n");
            sb.append(String.format("<@%s> (%d)", scoreEntry.getKey().getId(), scoreEntry.getValue()));
        });
        final String winnersText = sb.toString();
        wordleChannel.sendMessage(msgTitle + winnersText).queue();
    }

    private List<Map.Entry<User, Integer>> getLowestScores(Map<User, Integer> scores) {
        int lowestScore = scores.values().stream()
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);

        if (lowestScore == Integer.MAX_VALUE) {
            return null;
        }

        return scores.entrySet().stream()
                .filter(entry -> entry.getValue() == lowestScore)
                .collect(Collectors.toList());
    }

    private boolean isWordleSubmission(String message) {
        final String[] split = message.split(" ");
        if (split.length < 3) {
            return false;
        }

        return "Wordle".equals(split[0]);
    }

    private int getScore(String message) {
        final String[] split = message.split(" ");
        if (split.length < 3) {
            return Integer.MAX_VALUE;
        }

        final String scoreString = split[2];
        final String[] scoreArray = scoreString.split("/");
        if (scoreArray.length != 2 || "X".equals(scoreArray[0])) {
            return Integer.MAX_VALUE;
        }

        return Integer.parseInt(scoreArray[0]);
    }


    @Override
    protected long getInitialDelay() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        final ZonedDateTime midnight = now.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1);

        return Duration.between(now, midnight).toMillis();
    }

    @Override
    protected long getDelay() {
        return Duration.ofDays(1).toMillis();
    }
}
