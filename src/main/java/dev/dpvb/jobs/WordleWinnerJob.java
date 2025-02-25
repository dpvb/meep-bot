package dev.dpvb.jobs;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.WordleEntry;
import dev.dpvb.mongo.services.WordleEntryService;
import dev.dpvb.util.Constants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WordleWinnerJob extends Job {

    private final JDA jda;

    public WordleWinnerJob(JDA jda) {
        this.jda = jda;
    }

    public void run() {
        final TextChannel wordleChannel = jda.getTextChannelById(Constants.Wordle.CHANNEL_ID);
        if (wordleChannel == null) {
            System.out.println("Error: Could not retreive Wordle Channel");
            return;
        }

        final WordleEntryService wes = MongoManager.getInstance().getWordleEntryService();

        final LocalDate yesterday = LocalDate.now().minusDays(1);
        final int wordleNumber = Period.between(Constants.Wordle.INITIAL_DATE, yesterday).getDays();

        final List<WordleEntry> wordleEntries = wes.getEntriesByWordleNumber(wordleNumber);

        final Map<String, Integer> scores = new HashMap<>();

        // Filter out -1 scores and put the potential winning scores in the map.
        wordleEntries.stream()
                .filter(wordleEntry -> wordleEntry.message.getGuessCount() != -1)
                .forEach(wordleEntry -> scores.put(wordleEntry.getDiscordID(), wordleEntry.getMessage().getGuessCount()));

        final List<Map.Entry<String, Integer>> lowestScores = getLowestScores(scores);

        if (lowestScores == null || lowestScores.isEmpty()) {
            wordleChannel.sendMessage("No one submitted anything for the Wordle today :C").queue();
            return;
        }

        final String msgTitle = "\uD83C\uDFC6 **Wordle Winners Today** \uD83C\uDFC6";
        final StringBuilder sb = new StringBuilder();
        lowestScores.forEach(scoreEntry -> {
            sb.append("\n");
            sb.append(String.format("<@%s> (%d)", scoreEntry.getKey(), scoreEntry.getValue()));
        });
        final String winnersText = sb.toString();
        wordleChannel.sendMessage(msgTitle + winnersText).queue();
    }

    private List<Map.Entry<String, Integer>> getLowestScores(Map<String, Integer> scores) {
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

    @Override
    protected long getInitialDelay() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        final ZonedDateTime three_am_next_day = now.withHour(3).withMinute(0).withSecond(0).withNano(0).plusDays(1);

        return Duration.between(now, three_am_next_day).toMillis();
    }

    @Override
    protected long getDelay() {
        return Duration.ofDays(1).toMillis();
    }
}
