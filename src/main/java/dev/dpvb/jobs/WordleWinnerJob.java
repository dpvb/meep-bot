package dev.dpvb.jobs;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.models.WordleEntry;
import dev.dpvb.mongo.services.WordleEntryService;
import dev.dpvb.util.Constants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordleWinnerJob extends Job {

    private final JDA jda;

    public WordleWinnerJob(JDA jda) {
        this.jda = jda;
    }

    public void run() {
        final TextChannel wordleChannel = jda.getTextChannelById(Constants.Wordle.CHANNEL_ID);
        if (wordleChannel == null) {
            System.err.println("Error: Could not retrieve Wordle Channel");
            return;
        }

        final WordleEntryService wes = MongoManager.getInstance().getWordleEntryService();

        final LocalDate yesterday = LocalDate.now().minusDays(1);
        final int wordleNumber = Period.between(Constants.Wordle.INITIAL_DATE, yesterday).getDays();

        final List<WordleEntry> wordleEntries = wes.getEntriesByWordleNumber(wordleNumber);

        if (wordleEntries.isEmpty()) {
            wordleChannel.sendMessage("No one submitted anything for the Wordle today :C").queue();
            return;
        }

        // Filter out -1 scores and sort the rest
        final List<WordleEntry> sortedScores = wordleEntries.stream()
                .filter(entry -> entry.getMessage().getGuessCount() != -1)
                .sorted(Comparator.comparing(entry -> entry.getMessage().getGuessCount()))
                .toList();

        if (sortedScores.isEmpty()) {
            wordleChannel.sendMessage("No one won the Wordle today :C").queue();
            return;
        }

        int lowestScore = sortedScores.get(0).getMessage().getGuessCount();
        final List<WordleEntry> lowestScores = sortedScores.stream()
                .takeWhile(e -> e.getMessage().getGuessCount() == lowestScore)
                .toList();

        final String msgTitle = "\uD83C\uDFC6 **Wordle Winners Today** \uD83C\uDFC6";
        final String winnersText2 = Stream
                .concat(Stream.of(msgTitle), lowestScores.stream().map(WordleWinnerJob::entryToMessage))
                .collect(Collectors.joining("\n"));

        wordleChannel.sendMessage(winnersText2).queue();
    }

    private static String entryToMessage(WordleEntry scoreEntry) {
        return String.format("<@%s> (%d)", scoreEntry.getDiscordID(), scoreEntry.getMessage().getGuessCount());
    }

    @Override
    protected long getInitialDelay() {
        // start the job at midnight in Pacific Time since that is the latest
        // time zone in the server at the moment
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        final ZonedDateTime midnight = now.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1);

        return Duration.between(now, midnight).toMillis();
    }

    @Override
    protected long getDelay() {
        return Duration.ofDays(1).toMillis();
    }
}
